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

public class AutoXp extends Module  {


    public AutoXp() {
        super("AutoXP");

        this.setDescription("");

        this.setCategory("Combat");

        this.addSetting(new Setting("Cooldown", 0) {{
            this.setMin(0);
            this.setMax(20);
            this.setDescription("Speed at which you place XP");
        }});

    }

    private Integer cooldown = 0;

    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
        this.cooldown = 0;
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
    }




    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                if (WalksyClient.getClient().currentScreen != null) {
                    return;
                }
                if (WalksyClient.getClient().options.useKey.isPressed()) {
                    if (this.cooldown > 0) {
                        this.cooldown--;
                        break;
                    }
                    ItemStack mainHandStack = WalksyClient.getClient().player.getMainHandStack();
                    if (!mainHandStack.isOf(Items.EXPERIENCE_BOTTLE)) {
                        return;
                    }
                    WalksyClient.getClient().interactionManager.interactItem((PlayerEntity) WalksyClient.getClient().player, Hand.MAIN_HAND);
                    WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
                    this.cooldown = this.getIntSetting("Cooldown");

                }
            }
        }
    }
}

