package net.walksy.client.modules.walksymodules;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.BlockUtils;
import net.walksy.client.utils.CrystalUtils;
import net.walksy.client.utils.RotationUtils;
import net.walksy.client.walksyevent.events.ItemUseListener;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.event.InputEvent;

public class AutoDtap extends Module implements ItemUseListener  {


    public AutoDtap() {
        super("AutoDtap");

        this.setDescription("Uses damage ticks to justify when to crystal");

        this.setCategory("Combat");


        this.addSetting(new Setting("BreakDelay", 0) {{
            this.setMin(0);
            this.setMax(20);
            this.setDescription("Speed at which you destroy crystals");
        }});

        //this.addSetting(new Setting("DamageTick", 8) {{
        //    this.setMin(0);
        //    this.setMax(10);
        //    this.setDescription("At which damage tick to activate on 1 being the start");
        //}});

        this.addSetting(new Setting("StopOnKill", false) {{
            this.setDescription("Stops crystaling when a player dies nearby");
        }});

    }

    private Integer breakInterval = 0;
    private int crystaled;

    private String lastTargetUuid = null;

    private int crystalBreakClock = 0;
    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
        crystalBreakClock = 0;
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
    }


    private boolean isDeadBodyNearby()
    {
        return WalksyClient.getClient().world.getPlayers().parallelStream()
                .filter(e -> WalksyClient.getClient().player != e)
                .filter(e -> e.squaredDistanceTo(WalksyClient.getClient().player) < 36)
                .anyMatch(LivingEntity::isDead);
    }

    private boolean canEntityBeHit() {
        return WalksyClient.getClient().world.getPlayers().parallelStream()
                .filter(e -> WalksyClient.getClient().player != e)
                .filter(e -> e.squaredDistanceTo(WalksyClient.getClient().player) < 25)
                .anyMatch(e -> e.hurtTime != 0);
    }




    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                boolean dontBreakCrystal = crystalBreakClock != 0;
                if (dontBreakCrystal)
                    crystalBreakClock--;
                if (GLFW.glfwGetMouseButton(WalksyClient.getClient().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS)
                    return;
                ItemStack mainHandStack = WalksyClient.getClient().player.getMainHandStack();
                if (!mainHandStack.isOf(Items.END_CRYSTAL))
                    return;
                if (this.getBoolSetting("StopOnKill") && isDeadBodyNearby())
                    return;
                Vec3d camPos = WalksyClient.getClient().player.getEyePos();
                BlockHitResult blockHit = WalksyClient.getClient().world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, WalksyClient.getClient().player));
                HitResult hitEntity = WalksyClient.getClient().crosshairTarget;
                if (hitEntity.getType() != HitResult.Type.ENTITY)
                    return;
                Entity target = ((EntityHitResult) hitEntity).getEntity();
                lastTargetUuid = target.getName().toString();
                if ((target instanceof PlayerEntity))
                    crystaled = 0;
                if (crystaled == 2) {
                    crystaled = 0;
                }
                if (!WalksyClient.getClient().options.useKey.isPressed()) {
                    crystaled = 0;
                }
                //TEST
                if (crystaled == 1 && !canEntityBeHit()) {
                    return;
                }
                if (WalksyClient.getClient().crosshairTarget instanceof EntityHitResult hit) {
                    if (!dontBreakCrystal && hit.getEntity() instanceof EndCrystalEntity crystal) {
                        crystalBreakClock = breakInterval;
                        WalksyClient.getClient().interactionManager.attackEntity(WalksyClient.getClient().player, crystal);
                        WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
                        WalksyClient.getInstance().getCrystalDataTracker().recordAttack(crystal);
                        this.breakInterval = this.getIntSetting("BreakDelay");
                        crystaled++;
                    }
                }
                if (BlockUtils.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos()) || BlockUtils.isBlock(Blocks.BEDROCK, blockHit.getBlockPos())) {
                    if (CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos())) {
                        ActionResult result = WalksyClient.getClient().interactionManager.interactBlock(WalksyClient.getClient().player, Hand.MAIN_HAND, blockHit);
                        if (result.isAccepted() && result.shouldSwingHand())
                            WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);

                    }
                }
            }
        }
    }



    @Override
    public void onItemUse(ItemUseEvent event)
    {
        ItemStack mainHandStack = WalksyClient.getClient().player.getMainHandStack();
        if (WalksyClient.getClient().crosshairTarget.getType() == HitResult.Type.BLOCK)
        {
            BlockHitResult hit = (BlockHitResult) WalksyClient.getClient().crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos()) || mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils.isBlock(Blocks.BEDROCK, hit.getBlockPos()))
                event.cancel();
        }
    }
}

