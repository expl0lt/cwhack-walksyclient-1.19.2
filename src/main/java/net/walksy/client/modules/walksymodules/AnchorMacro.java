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

public class AnchorMacro extends Module {


    public AnchorMacro() {
        super("AnchorMacro");

        this.setDescription("Anchor Macro");

        this.setCategory("Combat");

        this.addSetting(new Setting("ChargeOnly", false) {{
            this.setDescription("Only charges the anchor");
        }});

        //this.addSetting(new Setting("SafeAnchor", false) {{
        //    this.setDescription("(1.19.4 ONLY)");
        //}});

        this.addSetting(new Setting("ItemSwap", 1) {{
            this.setMax(10);
            this.setMin(0);
            this.setDescription("Item to swap to after exploding the anchor");
        }});

        this.addSetting(new Setting("Cooldown", 0) {{
            this.setMax(20);
            this.setMin(0);
            this.setDescription("Cooldown between blowing up anchors");
        }});

    }
    private boolean hasAnchored;
    private boolean hasCharged;
    private int clock;



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
                if (WalksyClient.getClient().player.isUsingItem()) {
                    return;
                }
                if (this.hasAnchored) {
                    if (this.clock != 0) {
                        --this.clock;
                        return;
                    }
                    this.clock = this.getIntSetting("Cooldown");
                    this.hasAnchored = false;
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
                    } else if (BlockUtils.isAnchorCharged(pos) && !this.getBoolSetting("ChargeOnly")) {
                        final PlayerInventory inv = WalksyClient.getClient().player.getInventory();
                        inv.selectedSlot = this.getIntSetting("ItemSwap");
                        final ActionResult actionResult2 = WalksyClient.getClient().interactionManager.interactBlock(WalksyClient.getClient().player, Hand.MAIN_HAND, hit);
                        if (actionResult2.isAccepted() && actionResult2.CONSUME.shouldSwingHand()) {
                            WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
                        }
                        this.hasAnchored = true;
                    }
                }
            }
        }
    }
}

