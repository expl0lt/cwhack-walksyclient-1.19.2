package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

public class GhostUse extends Module {


    public GhostUse() {
        super("GhostUse");

        this.setDescription("Allows you to use things without holding them");
        this.addSetting(new Setting("Obsidian", false) {{
        }});

        this.addSetting(new Setting("NoHold", false) {{
        }});

        this.setCategory("Combat");

    }

    //TODO DEACTIVATE ZEROOBIANCHORPLAFCEDELAY

    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
    }

    private boolean isHeld = false;
    private boolean hasPlaced = false;

    private boolean hasPlacedRespawnAnchor = false;

    private boolean hasPlacedGlowstone = false;


    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                if (this.getBoolSetting("Obsidian")) {
                    if (!WalksyClient.getClient().options.useKey.isPressed()) {
                        hasPlaced = false;
                    }
                    if (hasPlaced && this.getBoolSetting("NoHold"))
                        return;
                    ItemStack mainHand = WalksyClient.getClient().player.getMainHandStack();
                    if (mainHand.isOf(Items.OBSIDIAN))
                        return;
                    Vec3d camPos = WalksyClient.getClient().player.getEyePos();
                    BlockHitResult blockHit = WalksyClient.getClient().world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, WalksyClient.getClient().player));
                    if (BlockUtils.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos()) || BlockUtils.isBlock(Blocks.AIR, blockHit.getBlockPos()))
                        return;

                    ItemStack item = WalksyClient.getClient().player.getStackInHand(WalksyClient.getClient().player.getActiveHand());
                    Item type = item.getItem();
                    if (WalksyClient.getClient().options.useKey.isPressed()) {
                        if (InventoryUtils.nameContains("totem")) {
                            InventoryUtils.search(Items.OBSIDIAN);
                            BlockPos pos = blockHit.getBlockPos();
                            BlockUtils.interact(pos, blockHit.getSide());
                            hasPlaced = true;
                            InventoryUtils.search(type);
                        }
                    }
                }
                /*
                if (this.getBoolSetting("Anchor&Glowstone")) {
                    if (!WalksyClient.getClient().options.useKey.isPressed()) {
                        hasPlacedRespawnAnchor = false;
                        hasPlacedGlowstone = false;
                    }
                    if (hasPlacedRespawnAnchor && this.getBoolSetting("NoHold"))
                        return;

                    if (hasPlacedGlowstone && this.getBoolSetting("NoHold"))
                        return;
                    ItemStack mainHand = WalksyClient.getClient().player.getMainHandStack();
                    if (mainHand.isOf(Items.RESPAWN_ANCHOR))
                        return;
                    Vec3d camPos = WalksyClient.getClient().player.getEyePos();
                    BlockHitResult blockHit = WalksyClient.getClient().world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, WalksyClient.getClient().player));
                    BlockPos pos = blockHit.getBlockPos();
                    if (BlockUtils.isBlock(Blocks.AIR, blockHit.getBlockPos()))
                        return;
                    if (BlockUtils.isAnchorCharged(pos)) {
                        if (WalksyClient.getClient().options.useKey.isPressed())
                            return;
                    }
                    ItemStack item = WalksyClient.getClient().player.getStackInHand(WalksyClient.getClient().player.getActiveHand());
                    Item type = item.getItem();
                    if (WalksyClient.getClient().options.useKey.isPressed()) {
                        if (InventoryUtils.nameContains("totem")) {
                            if (BlockUtils.isAnchorUncharged(blockHit.getBlockPos())) {
                                InventoryUtils.search(Items.GLOWSTONE);
                                BlockUtils.interact(pos, blockHit.getSide());
                                hasPlacedGlowstone = true;
                                InventoryUtils.search(type);
                            }
                            if (BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, blockHit.getBlockPos()))
                                return;
                            InventoryUtils.search(Items.RESPAWN_ANCHOR);
                            BlockUtils.interact(pos, blockHit.getSide());
                            hasPlacedRespawnAnchor = true;
                            InventoryUtils.search(type);
                        }
                    }
                }
            }


                if (this.getBoolSetting("Sword")) {
                    ItemStack item = WalksyClient.getClient().player.getStackInHand(WalksyClient.getClient().player.getActiveHand());
                    Item type = item.getItem();
                    HitResult hit = WalksyClient.getClient().crosshairTarget;
                    if (hit.getType() != HitResult.Type.ENTITY) {
                        InventoryUtils.search(type);
                        return;
                    }
                    Entity target = ((EntityHitResult) hit).getEntity();
                    if (!(target instanceof PlayerEntity))
                        return;

                    InventoryUtils.selectItemFromHotbar(Items.DIAMOND_SWORD);
                    InventoryUtils.selectItemFromHotbar(Items.NETHERITE_SWORD);
                }
            }

                 */
            }
        }
    }
}






