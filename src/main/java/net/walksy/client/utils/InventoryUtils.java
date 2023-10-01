package net.walksy.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.walksy.client.WalksyClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.walksy.client.interfaces.mixin.IClientPlayerInteractionManager;

public class InventoryUtils {

    private static final Action ACTION = new Action();
    public static int previousSlot = -1;


    public static boolean search(Item item) {
        final PlayerInventory inv = WalksyClient.getClient().player.getInventory();
        for (int i = 0; i <= 8; i ++) {
            if (inv.getStack(i).isOf(item)) {
                inv.selectedSlot = i;
                return true;
            }
        }
        return false;
    }

    public static boolean nameContains(String contains) {
        return nameContains(contains, Hand.MAIN_HAND);
    }

    /**
     * If the player's held item in thee specified hand's name contains the following string
     * @param contains string to check
     * @param hand hand to check
     * @return match
     */
    public static boolean nameContains(String contains, Hand hand) {
        ItemStack item = WalksyClient.getClient().player.getStackInHand(hand);
        return item != null && item.getTranslationKey().toLowerCase().contains(contains.toLowerCase());
    }




    public static List<Integer> getItemSlots(int total, Predicate<ItemStack> isItem) {
        List<Integer> slots = new ArrayList<Integer>();

        PlayerInventory inv = WalksyClient.me().getInventory();

        // Iterate over all items that are not armour, crafting, off-hand or hotbar.
        for (int i = 9; i < PlayerInventory.MAIN_SIZE && total > 0; i++) {
            if (isItem.test(inv.main.get(i))) {
                total--;
                slots.add(i);
            }
        }

        // Check the hotbar
        for (int i = 0; i < 9 && total > 0; i++) {
            if (isItem.test(inv.main.get(i))) {
                total--;
                slots.add(i + 36);
            }
        }

        return slots;
    }


    public static boolean selectItemFromHotbar(Predicate<Item> item) {
        PlayerInventory inv = WalksyClient.getClient().player.getInventory();
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = inv.getStack(i);
            if (!item.test(itemStack.getItem())) continue;
            inv.selectedSlot = i;
            return true;
        }
        return false;
    }
    public static boolean selectItemFromHotbar (Item item){
        return selectItemFromHotbar((Item i) -> i == item);
    }

    public static int getSlotWithItem(Item i) {
        List<Integer> slots = getItemSlots(1, item -> item.isOf(i));

        if (slots.size() == 0) return -1;

        return slots.get(0);
    }

    public static void moveItem(int from, int to) {
        if (from == to) return;

        // Check if it was empty before since if we move something into it, it won't be empty anymore.
        boolean wasEmpty = WalksyClient.me().getInventory().getStack(to).isEmpty();

        // Click the slot
        InteractionUtils.pickupItem(from);

        // Click off 'to' slot (this may pick up an item that was already in there.)
        InteractionUtils.pickupItem(to);

        if (!wasEmpty) {
            // So if it wasn't empty, we will now have the item that was previously in the 'to' slot now at our finger
            // in that case, we place it where the totem used to be and call it a day.
            InteractionUtils.pickupItem(from);
        }
    }

    public static int getMainHandSlot() {
        ClientPlayerEntity me = WalksyClient.me();
        PlayerInventory inv = me.getInventory();

        return inv.selectedSlot + 36;
    }

    public static int getFreeSlot() {
        int slot = WalksyClient.me().getInventory().getEmptySlot();

        if (slot == -1) return -1;

        // Handle the hotbar
        if (slot < 9) return slot + 36;

        // Others
        return slot;
    }


    public static int countItem(Predicate<Item> item)
    {
        PlayerInventory inv = WalksyClient.getClient().player.getInventory();

        int count = 0;

        for (int i = 0; i < 36; i++)
        {
            ItemStack itemStack = inv.getStack(i);
            if (item.test(itemStack.getItem()))
                count += itemStack.getCount();
        }

        return count;
    }

    public static int countItem(Item item)
    {
        return countItem(i -> i == item);
    }






    public static Action move() {
        ACTION.type = SlotActionType.PICKUP;
        ACTION.two = true;
        return ACTION;
    }

    public static Action click() {
        ACTION.type = SlotActionType.PICKUP;
        return ACTION;
    }

    public static class Action {
        private SlotActionType type = null;
        private boolean two = false;
        private int from = -1;
        private int to = -1;
        private int data = 0;

        private boolean isRecursive = false;

        private Action() {}


        public void slotId(int id) {
            from = to = id;
            run();
        }
        public Action fromId(int id) {
            from = id;
            return this;
        }
        public Action from(int index) {
            return fromId(SlotUtils.indexToId(index));

        }
        public void toId(int id) {
            to = id;
            run();
        }

        public void to(int index) {
            toId(SlotUtils.indexToId(index));
        }



        private void run() {
            boolean hadEmptyCursor = WalksyClient.getClient().player.currentScreenHandler.getCursorStack().isEmpty();

            if (type != null && from != -1 && to != -1) {
                click(from);
                if (two) click(to);
            }

            SlotActionType preType = type;
            boolean preTwo = two;
            int preFrom = from;
            int preTo = to;

            type = null;
            two = false;
            from = -1;
            to = -1;
            data = 0;

            if (!isRecursive && hadEmptyCursor && preType == SlotActionType.PICKUP && preTwo && (preFrom != -1 && preTo != -1) && !WalksyClient.getClient().player.currentScreenHandler.getCursorStack().isEmpty()) {
                isRecursive = true;
                InventoryUtils.click().slotId(preFrom);
                isRecursive = false;
            }
        }
        private void click(int id) {
            WalksyClient.getClient().interactionManager.clickSlot(WalksyClient.getClient().player.currentScreenHandler.syncId, id, data, type, WalksyClient.getClient().player);
        }
    }


    public static boolean swap(int slot, boolean swapBack) {
        if (slot < 0 || slot > 8) return false;
        if (swapBack && previousSlot == -1) previousSlot = WalksyClient.getClient().player.getInventory().selectedSlot;

        WalksyClient.getClient().player.getInventory().selectedSlot = slot;
        ((IClientPlayerInteractionManager) WalksyClient.getClient().interactionManager).syncSelected();
        return true;
    }

    public static boolean swapBack() {
        if (previousSlot == -1) return false;

        boolean return_ = swap(previousSlot, false);
        previousSlot = -1;
        return return_;
    }
}
