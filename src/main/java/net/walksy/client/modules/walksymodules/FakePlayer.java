package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.OtherClientPlayerEntity;
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

public class FakePlayer extends Module  {


    public FakePlayer() {
        super("FakePlayer");

        this.setDescription("Spawns a fake player");

        this.setCategory("Combat");

        this.addSetting(new Setting("FakePlayerName", "Walksy") {{
            this.setDescription("");
        }});

    }
    int id;
    @Override
    public void activate() {
        if (WalksyClient.getClient().world == null) {
            return;
        }
        OtherClientPlayerEntity player = new OtherClientPlayerEntity(WalksyClient.getClient().world, new GameProfile(null, this.getStringSetting("FakePlayerName")), null);
        Vec3d pos = WalksyClient.getClient().player.getPos();
        player.updateTrackedPosition(pos.x,pos.y,pos.z);
        player.updatePositionAndAngles(pos.x, pos.y, pos.z, WalksyClient.getClient().player.getYaw(), WalksyClient.getClient().player.getPitch());
        player.resetPosition();
        WalksyClient.getClient().world.addPlayer(player.getId(), player);
        id = player.getId();
    }


    @Override
    public void deactivate() {
        WalksyClient.getClient().world.removeEntity(id, Entity.RemovalReason.DISCARDED);
        this.removeListen(ClientTickEvent.class);
    }




    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {


            }
        }
    }
}

