package net.walksy.client.modules.render;

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
import net.walksy.client.events.render.OnRenderEvent;
import net.walksy.client.events.render.Render3DEvent;
import net.walksy.client.misc.Colour;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;

public class CrystalPlaceCheck extends Module  {


    public CrystalPlaceCheck() {
        super("CrystalPlaceCheck");

        this.setDescription("checks if you can place obsidian on the obsidian");

        this.setCategory("Render");


    }


    @Override
    public void activate() {
        this.addListen(OnRenderEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(OnRenderEvent.class);
    }


    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "OnRenderEvent": {
                OnRenderEvent e = ((OnRenderEvent) event);
                Colour cannotPlaceColour = WalksyClient.getInstance().config.unableToPlaceCrystalColour;
                Colour canPlaceColour = WalksyClient.getInstance().config.canPlaceCrystalColour;
                Vec3d camPos = WalksyClient.getClient().player.getEyePos();
                BlockHitResult blockHit = WalksyClient.getClient().world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, WalksyClient.getClient().player));
                if (!BlockUtils.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos()))
                    return;
                if (CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos())) {
                    RenderUtils.renderFilledBlockBox(e.mStack, blockHit.getBlockPos(), canPlaceColour.r, canPlaceColour.g, canPlaceColour.b, canPlaceColour.a);
                } else if (!CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos())) {
                    RenderUtils.renderFilledBlockBox(e.mStack, blockHit.getBlockPos(), cannotPlaceColour.r, cannotPlaceColour.g, cannotPlaceColour.b, cannotPlaceColour.a);
                }
                break;
            }
        }
    }
}




