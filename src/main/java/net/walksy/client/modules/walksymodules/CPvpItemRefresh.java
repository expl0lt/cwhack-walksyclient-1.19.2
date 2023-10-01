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
import net.walksy.client.mixin.client.ItemStackAccessor;
import net.walksy.client.modules.Module;
import net.walksy.client.modules.render.Ambience;
import net.walksy.client.utils.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;

public class CPvpItemRefresh extends Module {



    private final ItemStack[] items = new ItemStack[10];


    public CPvpItemRefresh() {
        super("CPvpItemRefresh");

        this.setDescription("Automatically refreshes items in your hotbar");

        this.setCategory("Combat");

        this.addSetting(new Setting("CpvpItems", false) {{
            this.setDescription("Shows the available items to refresh");
        }});

        //Items
        this.addSetting(new CpvpItems("Crystals", false) {{
        }});
        this.addSetting(new CpvpItems("Pearls", false) {{
        }});
        this.addSetting(new CpvpItems("Obsidian", false) {{
        }});
        this.addSetting(new CpvpItems("Gapples", false) {{
        }});
        this.addSetting(new CpvpItems("Anchors", false) {{
        }});
        this.addSetting(new CpvpItems("GlowStone", false) {{
        }});
        this.addSetting(new CpvpItems("EXP", false) {{
        }});




        this.addSetting(new Setting("MinItemStack", 0) {{
            this.setMax(64);
            this.setMin(0);
            this.setDescription("The threshold of items left this actives at");
        }});

        this.addSetting(new Setting("SearchHotbar", false) {{
            this.setDescription("Checks hotbar for that item too");
        }});

        for (int i = 0; i < items.length; i++) items[i] = new ItemStack(Items.AIR);



    }

    @Override
    public void activate() {
        fillItems();
        this.addListen(ClientTickEvent.class);
        prevHadOpenScreen = WalksyClient.getClient().currentScreen != null;

    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
    }

    private boolean prevHadOpenScreen;
    private int tickDelayLeft;

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                //return statements
                ItemStack mainHand = WalksyClient.getClient().player.getMainHandStack();
                if (mainHand.isOf(Items.EXPERIENCE_BOTTLE) && this.getBoolSetting("EXP")
                        || mainHand.isOf(Items.GOLDEN_APPLE) && this.getBoolSetting("Gapples")
                        || mainHand.isOf(Items.OBSIDIAN) && this.getBoolSetting("Obsidian")
                        || mainHand.isOf(Items.GLOWSTONE) && this.getBoolSetting("GlowStone")
                        || mainHand.isOf(Items.ENDER_PEARL) && this.getBoolSetting("Pearls")
                        || mainHand.isOf(Items.RESPAWN_ANCHOR) && this.getBoolSetting("Anchors")
                        || mainHand.isOf(Items.END_CRYSTAL) && this.getBoolSetting("Crystals")) {
                    if (WalksyClient.getClient().currentScreen == null && prevHadOpenScreen) {
                        fillItems();
                    }

                    prevHadOpenScreen = WalksyClient.getClient().currentScreen != null;
                    if (WalksyClient.getClient().player.currentScreenHandler.getStacks().size() != 46 || WalksyClient.getClient().currentScreen != null)
                        return;

                    if (tickDelayLeft <= 0) {
                        tickDelayLeft = 0;

                        // Hotbar
                        for (int i = 0; i < 9; i++) {
                            ItemStack stack = WalksyClient.getClient().player.getInventory().getStack(i);
                            checkSlot(i, stack);
                        }
                    } else {
                        tickDelayLeft--;
                    }
                }
            }
        }
    }




    private void checkSlot(int slot, ItemStack stack) {
        ItemStack prevStack = getItem(slot);

        // Stackable items 1
        if (!stack.isEmpty() && stack.isStackable()) {
            if (stack.getCount() <= this.getIntSetting("MinItemStack")) {
                addSlots(slot, findItem(stack, slot, this.getIntSetting("MinItemStack") - stack.getCount() + 1));
            }
        }

        if (stack.isEmpty() && !prevStack.isEmpty()) {
            // Stackable items 2
            if (prevStack.isStackable()) {
                addSlots(slot, findItem(prevStack, slot, this.getIntSetting("MinItemStack") - stack.getCount() + 1));
            }
        }

        setItem(slot, stack);
    }


    private int findItem(ItemStack itemStack, int excludedSlot, int goodEnoughCount) {
        int slot = -1;
        int count = 0;

        for (int i = WalksyClient.getClient().player.getInventory().size() - 2; i >= (this.getBoolSetting("SearchHotbar") ? 0 : 9); i--) {
            ItemStack stack = WalksyClient.getClient().player.getInventory().getStack(i);

            if (i != excludedSlot && stack.getItem() == itemStack.getItem() && ItemStack.areNbtEqual(itemStack, stack)) {
                if (stack.getCount() > count) {
                    slot = i;
                    count = stack.getCount();

                    if (count >= goodEnoughCount) break;
                }
            }
        }

        return slot;
    }

    private void addSlots(int to, int from) {
        InventoryUtils.move().from(from).to(to);
    }

    private void fillItems() {
        for (int i = 0; i < 9; i++) {
            setItem(i, WalksyClient.getClient().player.getInventory().getStack(i));
        }

        setItem(SlotUtils.OFFHAND, WalksyClient.getClient().player.getOffHandStack());
    }

    private ItemStack getItem(int slot) {
        if (slot == SlotUtils.OFFHAND) slot = 9;

        return items[slot];
    }

    private void setItem(int slot, ItemStack stack) {
        if (slot == SlotUtils.OFFHAND) slot = 9;

        ItemStack s = items[slot];
        ((ItemStackAccessor) (Object) s).setItem(stack.getItem());
        s.setCount(stack.getCount());
        s.setNbt(stack.getNbt());
        ((ItemStackAccessor) (Object) s).setEmpty(stack.isEmpty());
    }


    private class CpvpItems extends Setting {
        public CpvpItems(String name, Object value) {
            super(name, value);

            this.setCategory("CpvpItems");
        }

        @Override
        public boolean shouldShow() {
            return getBoolSetting("CpvpItems");
        }
    }
}



