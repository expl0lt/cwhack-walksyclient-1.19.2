package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
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
import net.walksy.client.items.WalksyItemFactory;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

public class AutoInventoryTotem extends Module {


    public AutoInventoryTotem() {
        super("AutoInventoryTotem");

        this.setDescription("Auto puts a totem in offhand and totem slot");

        this.setCategory("Combat");

        this.addSetting(new Setting("HoverCurser", false) {{
            this.setDescription("Must have your curser hovered over a totem");
        }});

        this.addSetting(new Setting("TotemOnInv", false) {{
            this.setDescription("Switches to a totem when you open your inv");
        }});

        this.addSetting(new Setting("AutoSwitch", false) {{
            this.setDescription("Automatically switches to your totem slot");
        }});

        this.addSetting(new Setting("ForceTotem", false) {{
            this.setDescription("Replaces useless items");
        }});

        this.addSetting(new Setting("Delay", 0) {{
            this.setMax(20);
            this.setMin(0);
            this.setDescription("Delay for auto switch after opening inv");
        }});

        this.addSetting(new Setting("TotemSlot", 0) {{
            this.setMax(9);
            this.setMin(0);
            this.setDescription("Slot in which you want your totem to go to");
        }});

    }
    private int invClock = -1;


    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
        invClock = -1;

    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
    }


    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                PlayerInventory inv = WalksyClient.getClient().player.getInventory();
                if (WalksyClient.getClient().currentScreen != null && this .getBoolSetting("TotemOnInv"))
                    inv.selectedSlot = this.getIntSetting("TotemSlot");
                if (this.getBoolSetting("HoverCurser")) {
                    if (!(WalksyClient.getClient().currentScreen instanceof InventoryScreen)) {
                        this.invClock = -1;
                        return;
                    }
                    if (this.invClock == -1)
                        this.invClock = this.getIntSetting("Delay");
                    if (this.invClock > 0) {
                        this.invClock--;
                        return;
                    }
                    if (this.getBoolSetting("AutoSwitch"))
                        inv.selectedSlot = this.getIntSetting("TotemSlot");
                    if (((ItemStack) inv.offHand.get(0)).getItem() != Items.TOTEM_OF_UNDYING) {
                        Screen screen = (MinecraftClient.getInstance().currentScreen);
                        HandledScreen<?> gui = (HandledScreen) screen;
                        Slot slot = AccessorUtils.getSlotUnderMouse(gui);
                        if (slot == null)
                            return;
                        int SlotUnderMouse = AccessorUtils.getSlotUnderMouse(gui).getIndex();
                        if (SlotUnderMouse > 35)
                            return;
                        if (SlotUnderMouse < 0)
                            return;
                        if (SlotUnderMouse == 40)
                            return;
                        if (((ItemStack) inv.main.get(SlotUnderMouse)).getItem() == Items.TOTEM_OF_UNDYING)
                            WalksyClient.getClient().interactionManager.clickSlot(((InventoryScreen) WalksyClient.getClient().currentScreen).getScreenHandler().syncId, SlotUnderMouse, 40, SlotActionType.SWAP, (PlayerEntity) WalksyClient.getClient().player);
                        return;
                    }
                    ItemStack mainHand = (ItemStack) inv.main.get(inv.selectedSlot);
                    if (mainHand.isEmpty() || this.getBoolSetting("ForceTotem") && mainHand.getItem() != Items.TOTEM_OF_UNDYING) {
                        Screen screen = (MinecraftClient.getInstance().currentScreen);
                        HandledScreen<?> gui = (HandledScreen) screen;
                        Slot slot = AccessorUtils.getSlotUnderMouse(gui);
                         if (slot == null)
                            return;
                        int SlotUnderMouse = AccessorUtils.getSlotUnderMouse(gui).getIndex();
                        if (SlotUnderMouse > 35)
                            return;
                        if (SlotUnderMouse < 0)
                            return;
                        if (SlotUnderMouse == 40)
                            return;
                        if (SlotUnderMouse == this.getIntSetting("TotemSlot"))
                            return;
                        if (((ItemStack) inv.main.get(SlotUnderMouse)).getItem() == Items.TOTEM_OF_UNDYING)
                            WalksyClient.getClient().interactionManager.clickSlot(((InventoryScreen) WalksyClient.getClient().currentScreen).getScreenHandler().syncId, SlotUnderMouse, inv.selectedSlot, SlotActionType.SWAP, (PlayerEntity) WalksyClient.getClient().player);
                        return;
                    }
                } else {
                    if (!(WalksyClient.getClient().currentScreen instanceof InventoryScreen)) {
                        invClock = -1;
                        return;
                    }
                    if (invClock == -1)
                        invClock = this.getIntSetting("Delay");
                    if (invClock > 0) {
                        invClock--;
                        return;
                    }
                    if (this.getBoolSetting("AutoSwitch"))
                        inv.selectedSlot = this.getIntSetting("TotemSlot");
                    if (inv.offHand.get(0).getItem() != Items.TOTEM_OF_UNDYING) {
                        int slot = findTotemSlot();
                        if (slot != -1) {
                            WalksyClient.getClient().interactionManager.clickSlot(((InventoryScreen) WalksyClient.getClient().currentScreen).getScreenHandler().syncId, slot, 40, SlotActionType.SWAP, WalksyClient.getClient().player);
                            return;
                        }
                    }
                    ItemStack mainHand = inv.main.get(inv.selectedSlot);
                    if (mainHand.isEmpty() ||
                            this.getBoolSetting("ForceTotem") && mainHand.getItem() != Items.TOTEM_OF_UNDYING) {
                        int slot = findTotemSlot();
                        if (slot != -1) {
                            WalksyClient.getClient().interactionManager.clickSlot(((InventoryScreen) WalksyClient.getClient().currentScreen).getScreenHandler().syncId, slot, inv.selectedSlot, SlotActionType.SWAP, WalksyClient.getClient().player);
                        }
                    }
                }
            }
        }
    }

            private int findTotemSlot()
            {
                PlayerInventory inv = WalksyClient.getClient().player.getInventory();
                for (int i = 9; i < 36; i++)
                {
                    if (inv.main.get(i).getItem() == Items.TOTEM_OF_UNDYING)
                        return i;
                }
                return -1;
            }
        }

