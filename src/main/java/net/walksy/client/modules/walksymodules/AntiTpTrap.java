package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
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
import net.walksy.client.events.client.JumpEvent;
import net.walksy.client.events.client.OpaqueCubeEvent;
import net.walksy.client.events.client.PlayerMoveEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerJumpListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

public class AntiTpTrap extends Module {





    public AntiTpTrap() {
        super("AntiTpTrap");

        this.setDescription("Allows you pearl out of anything");

        this.setCategory("Combat");

    }

    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
        this.addListen(JumpEvent.class);
        this.addListen(PlayerMoveEvent.class);
        this.addListen(OpaqueCubeEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
        this.removeListen(JumpEvent.class);
        this.removeListen(PlayerMoveEvent.class);
        this.removeListen(OpaqueCubeEvent.class);
    }




    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "OpaqueCubeEvent": {
                ((OpaqueCubeEvent)event).ci.cancel();
                break;
            }
            case "PlayerMoveEvent": {
                if (!collidingBlocks())
                    return;
                ClientPlayerEntity player = WalksyClient.getClient().player;
                player.noClip = true;
                break;
            }
            case "ClientTickEvent": {
                if (!collidingBlocks())
                    return;

                ClientPlayerEntity player = WalksyClient.getClient().player;

                player.noClip = true;
                player.fallDistance = 0;
                player.setOnGround(true);

                player.getAbilities().flying = false;
                player.setVelocity(0, 0, 0);

                float speed = 0.02F;
                player.airStrafingSpeed = speed;

                if (WalksyClient.getClient().options.jumpKey.isPressed()) {
                    player.addVelocity(0, speed, 0);
                }
                if (WalksyClient.getClient().options.sneakKey.isPressed()) {
                    player.addVelocity(0, -speed, 0);
                }
                break;
            }
            case "JumpEvent": {
                if (!collidingBlocks())
                    return;
                ((JumpEvent)event).ci.cancel();
                break;
            }
        }
    }

    private boolean collidingBlocks()
    {
        ClientPlayerEntity player = WalksyClient.getClient().player;
        return
                wouldCollideAt(new BlockPos(player.getX() - (double)player.getWidth() * 0.35D, player.getY(), player.getZ() + (double)player.getWidth() * 0.35D)) ||
                        wouldCollideAt(new BlockPos(player.getX() - (double)player.getWidth() * 0.35D, player.getY(), player.getZ() - (double)player.getWidth() * 0.35D)) ||
                        wouldCollideAt(new BlockPos(player.getX() + (double)player.getWidth() * 0.35D, player.getY(),player.getZ() - (double)player.getWidth() * 0.35D)) ||
                        wouldCollideAt(new BlockPos(player.getX() + (double)player.getWidth() * 0.35D, player.getY(),player.getZ() + (double)player.getWidth() * 0.35D));
    }

    private boolean wouldCollideAt(BlockPos pos)
    {
        Box box = WalksyClient.getClient().player.getBoundingBox();
        Box box2 = (new Box(pos.getX(), box.minY, pos.getZ(), (double)pos.getX() + 1.0D, box.maxY, (double)pos.getZ() + 1.0D)).contract(1.0E-7D);
        return WalksyClient.getClient().world.canCollide(WalksyClient.getClient().player, box2);
    }
}



