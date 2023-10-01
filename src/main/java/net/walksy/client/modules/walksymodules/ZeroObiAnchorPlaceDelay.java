package net.walksy.client.modules.walksymodules;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.BlockUtils;
import net.walksy.client.utils.InventoryUtils;
import net.walksy.client.utils.RotationUtils;

public class ZeroObiAnchorPlaceDelay extends Module {


    public ZeroObiAnchorPlaceDelay() {
        super("ZeroObiAnchorPlaceDelay");

        this.setDescription("Place obi and anchors faster, less delay");

        this.setCategory("Combat");

        this.addSetting(new Setting("Anchors", true) {{
            this.setDescription("Stops when a player dies nearby");
        }});

        this.addSetting(new Setting("Obsidian", true) {{
            this.setDescription("Stops when a player dies nearby");
        }});

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

    private boolean hasPlaced = false;
    private boolean hasPlacedGlowstone = false;
    private boolean hasPlacedRespawnAnchor = false;


    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                if (this.getBoolSetting("Obsidian")) {
                    if (!WalksyClient.getClient().options.useKey.isPressed()) {
                        hasPlaced = false;
                    }
                    ItemStack mainHandStack = WalksyClient.getClient().player.getMainHandStack();
                    if (!mainHandStack.isOf(Items.OBSIDIAN))
                        return;

                    if (hasPlaced)
                        return;
                    Vec3d camPos = WalksyClient.getClient().player.getEyePos();
                    BlockHitResult hit = WalksyClient.getClient().world.raycast(new RaycastContext(camPos, camPos.add(getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, WalksyClient.me()));
                    if (BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos()))
                        return;
                    if (BlockUtils.isBlock(Blocks.AIR, hit.getBlockPos()) || BlockUtils.isBlock(Blocks.WATER, hit.getBlockPos()))
                        return;

                    if (WalksyClient.getClient().options.useKey.isPressed()) {
                        BlockPos pos = hit.getBlockPos();
                        BlockUtils.interact(pos, hit.getSide());
                        hasPlaced = true;
                    }
                }
                if (this.getBoolSetting("Anchors")) {
                    if (!WalksyClient.getClient().options.useKey.isPressed()) {
                        hasPlacedRespawnAnchor = false;
                        hasPlacedGlowstone = false;
                    }
                    if (hasPlacedRespawnAnchor)
                        return;

                    if (hasPlacedGlowstone)
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
        }
    }
}

