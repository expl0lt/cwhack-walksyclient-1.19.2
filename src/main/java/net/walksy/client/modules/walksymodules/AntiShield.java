package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.walksy.client.Main;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.BlockUtils;
import net.walksy.client.utils.ClientUtils;
import net.walksy.client.utils.CrystalUtils;
import net.walksy.client.utils.InteractionUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

public class AntiShield extends Module  {

    public AntiShield() {
        super("AntiShield");

        this.setDescription("Bybasses shields allowing you to still hit them");

        //this.addSetting(new Setting("MustHit", false) {{
        //    this.setDescription("Have to hit the player to disable the shield");
        //}});

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




    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                ItemStack mainHand = WalksyClient.getClient().player.getMainHandStack();
                HitResult hit = WalksyClient.getClient().crosshairTarget;
                if (hit.getType() != HitResult.Type.ENTITY)
                    return;
                Entity target = ((EntityHitResult) hit).getEntity();
                if (!(target instanceof PlayerEntity))
                    return;
                if (!((PlayerEntity) target).isUsingItem()) {
                    return;
                }
                if ((mainHand.isOf(Items.DIAMOND_SWORD) || mainHand.isOf(Items.NETHERITE_SWORD))) {
                    if (((PlayerEntity) target).getOffHandStack().getItem() instanceof ShieldItem || ((PlayerEntity) target).getMainHandStack().getItem() instanceof ShieldItem) {
                        selectItemFromHotbar(Items.NETHERITE_AXE);
                        selectItemFromHotbar(Items.DIAMOND_AXE);
                    }
                }

                if ((mainHand.isOf(Items.NETHERITE_AXE) || mainHand.isOf(Items.DIAMOND_AXE))) {
                    if (((PlayerEntity) target).getOffHandStack().getItem() instanceof ShieldItem || ((PlayerEntity) target).getMainHandStack().getItem() instanceof ShieldItem) {
                        if (!WalksyClient.getClient().options.attackKey.isPressed())
                            return;
                        selectItemFromHotbar(Items.NETHERITE_SWORD);
                        if (((PlayerEntity) target).canTakeDamage()) {
                            WalksyClient.getClient().interactionManager.attackEntity(WalksyClient.getClient().player, target);
                            WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }



    public boolean selectItemFromHotbar(Predicate<Item> item) {
        PlayerInventory inv = WalksyClient.getClient().player.getInventory();
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = inv.getStack(i);
            if (!item.test(itemStack.getItem())) continue;
            inv.selectedSlot = i;
            return true;
        }
        return false;
    }
    public boolean selectItemFromHotbar (Item item){
        return this.selectItemFromHotbar((Item i) -> i == item);
    }
}

