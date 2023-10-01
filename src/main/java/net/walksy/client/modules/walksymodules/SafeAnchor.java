package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
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
import net.walksy.client.utils.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

public class SafeAnchor extends Module {


    public SafeAnchor() {
        super("SafeAnchor");

        this.setDescription("Anchor without taking any damage");

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
                if (GLFW.glfwGetMouseButton(WalksyClient.getClient().getWindow().getHandle(), 1) != 1) {
                    return;
                }
                //GENIUS
                if (WalksyClient.getClient().player.getPitch() < 15.0F) {
                    return;
                }
                if (WalksyClient.getClient().player.isUsingItem()) {
                    return;
                }
                final HitResult cr = WalksyClient.getClient().crosshairTarget;
                if (cr instanceof BlockHitResult) {
                    final BlockHitResult hit = (BlockHitResult) cr;
                    final BlockPos pos = hit.getBlockPos();
                    if (BlockUtils.isAnchorUncharged(pos)) {
                        if (WalksyClient.getClient().player.isHolding(Items.GLOWSTONE)) {
                            final ActionResult actionResult = WalksyClient.getClient().interactionManager.interactBlock(WalksyClient.getClient().player, Hand.MAIN_HAND, hit);
                            if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                                WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
                            }
                            return;
                        }
                        InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
                        final ActionResult actionResult = WalksyClient.getClient().interactionManager.interactBlock(WalksyClient.getClient().player, Hand.MAIN_HAND, hit);
                        if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                            WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
                        }
                        if (BlockUtils.isAnchorCharged(pos)) {
                            setPressed(WalksyClient.getClient().options.sneakKey, true);
                        }
                    } else if (BlockUtils.isBlock(Blocks.GLOWSTONE, pos)) {
                        InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
                        ItemStack mainHand = WalksyClient.getClient().player.getMainHandStack();
                        if (mainHand.isOf(Items.TOTEM_OF_UNDYING)) {
                            unpress();
                        } else unpress();
                    }
                }
            }
        }
    }

    private void unpress() {
        setPressed(WalksyClient.getClient().options.sneakKey, false);
    }

    private void setPressed(KeyBinding key, boolean pressed) {
        key.setPressed(pressed);
        Input.setKeyState(key, pressed);
    }
}

