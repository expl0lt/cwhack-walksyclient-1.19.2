package net.walksy.client.modules.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
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

public class ShowDamageTick extends Module  {


    public ShowDamageTick() {
        super("PlayerState");

        this.setDescription("Renders what state other players are in");

        this.setCategory("Render");


    }
    public static boolean cannotBeHit;
    public static boolean isInAir;

    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
        cannotBeHit = false;
    }


    private boolean canEntityBeHit() {
        return WalksyClient.getClient().world.getPlayers().parallelStream()
                .filter(e -> WalksyClient.getClient().player != e)
                .filter(e -> e.squaredDistanceTo(WalksyClient.getClient().player) < 25)
                .noneMatch(e -> e.hurtTime > 0);
    }

    private boolean isEntityInAir() {
        return WalksyClient.getClient().world.getPlayers().parallelStream()
                .filter(e -> WalksyClient.getClient().player != e)
                .filter(e -> e.squaredDistanceTo(WalksyClient.getClient().player) < 25)
                .noneMatch(e -> !e.isOnGround());
    }




    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                if (!canEntityBeHit()) {
                    cannotBeHit = true;
                } else if (cannotBeHit) {
                    cannotBeHit = false;
                }
                if (!isEntityInAir()) {
                    isInAir = true;
                } else if (isInAir) {
                    isInAir = false;
                }
            }
        }
    }
}

