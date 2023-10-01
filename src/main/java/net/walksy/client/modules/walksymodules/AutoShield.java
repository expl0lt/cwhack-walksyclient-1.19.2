package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.explosion.Explosion;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

public class AutoShield extends Module  {
    private Double lastValid = WalksyClient.getCurrentTime();


    public AutoShield() {
        super("AutoShield");

        this.setDescription("..");

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

