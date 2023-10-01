package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.events.packet.PostMovementPacketEvent;
import net.walksy.client.events.packet.SendPacketEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.modules.hud.Watermark;
import net.walksy.client.utils.BlockUtils;
import net.walksy.client.utils.ClientUtils;
import net.walksy.client.utils.CrystalUtils;
import net.walksy.client.utils.InteractionUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.walksy.client.utils.BlockUtils.isBlock;
import static net.walksy.client.utils.BlockUtils.placeBlock;

public class ZeroCrystalPlaceDelay extends Module {

    public static boolean enabled = false;
    public ZeroCrystalPlaceDelay() {
        super("WalksyPlaceOptimizer");

        this.setDescription("Crystal like Walksy!");

        this.addSetting(new Setting("Legit", false) {{
            this.setDescription("Crystals now leave the obsidian");
        }});

        this.setCategory("Combat");


    }


    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
    }


    public static Vec3d getPlayerLookVec(PlayerEntity player) {
        float f = (float) Math.PI / 180;
        float pi = (float) Math.PI;
        float f1 = MathHelper.cos(-player.getYaw() * f - pi);
        float f2 = MathHelper.sin(-player.getYaw() * f - pi);
        float f3 = -MathHelper.cos(-player.getPitch() * f);
        float f4 = MathHelper.sin(-player.getPitch() * f);
        return new Vec3d(f2 * f3, f4, f1 * f3).normalize();
    }

    public static Vec3d getClientLookVec() {
        return getPlayerLookVec(WalksyClient.getClient().player);
    }


    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                ItemStack mainHandStack = WalksyClient.getClient().player.getMainHandStack();
                if (!mainHandStack.isOf(Items.END_CRYSTAL))
                    return;
                Vec3d camPos = WalksyClient.getClient().player.getEyePos();
                BlockHitResult blockHit = WalksyClient.getClient().world.raycast(new RaycastContext(camPos, camPos.add(getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, WalksyClient.getClient().player));
                if (WalksyClient.getClient().crosshairTarget instanceof EntityHitResult hit) {
                    if (WalksyClient.getClient().options.attackKey.wasPressed()) {
                        if (hit.getEntity() instanceof EndCrystalEntity crystal) {
                            WalksyClient.getClient().interactionManager.attackEntity(WalksyClient.getClient().player, crystal);
                            WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
                            WalksyClient.getInstance().getCrystalDataTracker().recordAttack(crystal);

                        } else if (hit.getEntity() instanceof MagmaCubeEntity magma) {

                            WalksyClient.getClient().interactionManager.attackEntity(WalksyClient.getClient().player, magma);
                            WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
                            WalksyClient.getInstance().getCrystalDataTracker().recordAttack(magma);
                        }
                    }
                }

                if (WalksyClient.getClient().options.useKey.isPressed()) {
                    if (this.getBoolSetting("Legit") && !CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos()))
                        return;
                    if (isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos())) {
                        ActionResult result = WalksyClient.getClient().interactionManager.interactBlock(WalksyClient.getClient().player, Hand.MAIN_HAND, blockHit);
                        if (result.isAccepted() && result.shouldSwingHand())
                            BlockUtils.interact(blockHit.getBlockPos(), blockHit.getSide());
                    }
                }
            }
        }
    }



    public static void onItemUse(CallbackInfo ci) {
        ItemStack mainHandStack = WalksyClient.getClient().player.getMainHandStack();
        if (WalksyClient.getClient().crosshairTarget.getType() == HitResult.Type.BLOCK)
        {
            BlockHitResult hit = (BlockHitResult) WalksyClient.getClient().crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos()) || mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils.isBlock(Blocks.BEDROCK, hit.getBlockPos()))
                ci.cancel();
        }
    }
}

