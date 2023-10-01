package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

public class AntiDoubleTap extends Module {





    public AntiDoubleTap() {
        super("AntiDoubleTap");

        this.setDescription("Automatically switches your mainhand to a totem in danger // Configed to be less annoying");

        this.setCategory("Combat");


        this.addSetting(new Setting("StopOnShield", false) {{
            this.setDescription("Stops working if holding a shield");
        }});
        this.addSetting(new Setting("StopInGui", false) {{
            this.setDescription("Stops working if a gui is open");
        }});

        this.addSetting(new Setting("DonutBypass", false) {{
            this.setDescription("Makes you use a slot");
        }});

        this.addSetting(new BypassSlot("TotemSlot", 0) {{
            this.setMax(9);
            this.setMin(1);
            this.setDescription("TotemSlot");
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

    private boolean cope = true;
    private boolean cope1 = true;
    private boolean cope2 = true;


    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                double dist = 6 * 6;
                PlayerInventory inv = WalksyClient.getClient().player.getInventory();
                if (WalksyClient.getClient().currentScreen != null && this.getBoolSetting("StopInGui")) {
                    return;
                }
                ItemStack mainHand = WalksyClient.getClient().player.getMainHandStack();
                if (mainHand.isOf(Items.SHIELD) && this.getBoolSetting("StopOnShield")) {
                    return;
                }
                if (WalksyClient.getClient().world.getPlayers().parallelStream().filter(e -> e != WalksyClient.getClient().player).noneMatch(player -> WalksyClient.getClient().player.squaredDistanceTo((Entity) player) <= dist)) {
                    return;
                }
                if (WalksyClient.getClient().world.getPlayers().parallelStream().filter(e -> e != WalksyClient.getClient().player).noneMatch(player -> WalksyClient.getClient().player.squaredDistanceTo((Entity) player) <= dist)) {
                    return;
                }
                double activatesAboveV = 0.2;
                int f = (int) Math.floor(activatesAboveV);
                for (int i = 1; i <= f; ++i) {
                    if (WalksyClient.getClient().world.getBlockState(WalksyClient.getClient().player.getBlockPos().add(0, -i, 0)).isAir()) continue;
                    return;
                }
                if (!WalksyClient.getClient().world.getBlockState(WalksyClient.getClient().player.getBlockPos().add(0.0, -activatesAboveV, 0.0)).isAir()) {
                    return;
                }
                List<EndCrystalEntity> crystals = this.getNearByCrystals();
                List<MagmaCubeEntity> magmas = this.getNearByMagmaCubes();
                ArrayList DayChecker = new ArrayList();
                magmas.forEach(e -> DayChecker.add(e.getPos()));
                crystals.forEach(e -> DayChecker.add(e.getPos()));
                if (this.cope) {
                    Stream<BlockPos> stream = this.getAllInBoxStream(WalksyClient.getClient().player.getBlockPos().add(-6, -8, -6), WalksyClient.getClient().player.getBlockPos().add(6, 2, 6)).filter(e -> this.isBlock(Blocks.OBSIDIAN, (BlockPos) e) || this.isBlock(Blocks.BEDROCK, (BlockPos) e)).filter(this::canPlaceCrystalClient);
                    if (this.cope1) {
                        stream = this.cope2 ? stream.filter(this::arePeopleAimingAtBlockAndHoldingCrystals) : stream.filter(this::arePeopleAimingAtBlock);
                    }
                    stream.forEachOrdered(e -> DayChecker.add(Vec3d.ofBottomCenter(e).add(0.0, 1.0, 0.0)));
                }
                for (Object pos : DayChecker) {
                    double damage = CrystalUtils.crystalDamage((PlayerEntity) WalksyClient.getClient().player, (Vec3d) pos, true, null, false);
                    if (!(damage >= (double) (WalksyClient.getClient().player.getHealth() + WalksyClient.getClient().player.getAbsorptionAmount()))) continue;
                    if (this.getBoolSetting("DonutBypass")) {
                        inv.selectedSlot = this.getIntSetting("TotemSlot");
                    }else
                        this.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
                    break;
                }
            }
        }
    }
    private List<EndCrystalEntity> getNearByCrystals() {
        Vec3d pos = WalksyClient.getClient().player.getPos();
        return WalksyClient.getClient().world.getEntitiesByClass(EndCrystalEntity.class, new Box(pos.add(-6.0, -6.0, -6.0), pos.add(6.0, 6.0, 6.0)), a -> true);
    }

    //DonutSmp Bybass
    private List<MagmaCubeEntity> getNearByMagmaCubes() {
        Vec3d pos = WalksyClient.getClient().player.getPos();
        return WalksyClient.getClient().world.getEntitiesByClass(MagmaCubeEntity.class, new Box(pos.add(-6.0, -6.0, -6.0), pos.add(6.0, 6.0, 6.0)), a -> true);
    }

    private boolean arePeopleAimingAtBlock(BlockPos block) {
        return WalksyClient.getClient().world.getPlayers().parallelStream().filter(e -> e != WalksyClient.getClient().player).anyMatch(e -> {
            Vec3d eyesPos = e.getEyePos();
            BlockHitResult hitResult = WalksyClient.getClient().world.raycast(new RaycastContext(eyesPos, eyesPos.add(this.getPlayerLookVec((PlayerEntity) e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity) e));
            return hitResult != null && hitResult.getBlockPos().equals(block);
        });
    }

    private boolean arePeopleAimingAtBlockAndHoldingCrystals(BlockPos block) {
        return WalksyClient.getClient().world.getPlayers().parallelStream().filter(e -> e != WalksyClient.getClient().player).filter(e -> e.isHolding(Items.END_CRYSTAL)).anyMatch(e -> {
            Vec3d eyesPos = e.getEyePos();
            BlockHitResult hitResult = WalksyClient.getClient().world.raycast(new RaycastContext(eyesPos, eyesPos.add(this.getPlayerLookVec((PlayerEntity) e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity) e));
            return hitResult != null && hitResult.getBlockPos().equals(block);
        });
    }

    public Vec3d getPlayerLookVec(PlayerEntity player) {
        float f = (float) Math.PI / 180;
        float pi = (float) Math.PI;
        float f1 = MathHelper.cos(-player.getYaw() * f - pi);
        float f2 = MathHelper.sin(-player.getYaw() * f - pi);
        float f3 = -MathHelper.cos(-player.getPitch() * f);
        float f4 = MathHelper.sin(-player.getPitch() * f);
        return new Vec3d(f2 * f3, f4, f1 * f3).normalize();
    }

    public Stream<BlockPos> getAllInBoxStream(BlockPos from, BlockPos to) {
        BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        Stream<BlockPos> stream = Stream.iterate(min, pos -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            if (++x > max.getX()) {
                x = min.getX();
                ++y;
            }
            if (y > max.getY()) {
                y = min.getY();
                ++z;
            }
            if (z > max.getZ()) {
                throw new IllegalStateException("Stream limit didn't work.");
            }
            return new BlockPos(x, y, z);
        });
        int limit = (max.getX() - min.getX() + 1) * (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1);
        return stream.limit(limit);
    }

    public boolean isBlock(Block block, BlockPos pos) {
        return this.getBlockState(pos).getBlock() == block;
    }

    public BlockState getBlockState(BlockPos pos) {
        return WalksyClient.getClient().world.getBlockState(pos);
    }

    public static boolean hasBlock(BlockPos pos)
    {
        return !WalksyClient.getClient().world.getBlockState(pos).isAir();
    }

    public boolean canPlaceCrystalClient(BlockPos block) {
        BlockState blockState = WalksyClient.getClient().world.getBlockState(block);
        if (!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.BEDROCK)) {
            return false;
        }
        return this.canPlaceCrystalClientAssumeObsidian(block);
    }

    public boolean canPlaceCrystalClientAssumeObsidian(BlockPos block) {
        BlockPos blockPos2 = block.up();
        if (!WalksyClient.getClient().world.isAir(blockPos2)) {
            return false;
        }
        double d = blockPos2.getX();
        double e = blockPos2.getY();
        double f = blockPos2.getZ();
        List list = WalksyClient.getClient().world.getOtherEntities(null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
        return list.isEmpty();
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


    private class BypassSlot extends Setting {
        public BypassSlot(String name, Object value) {
            super(name, value);

            this.setCategory("BypassSlot");
        }

        @Override
        public boolean shouldShow() {
            return getBoolSetting("DonutBypass");
        }
    }
}



