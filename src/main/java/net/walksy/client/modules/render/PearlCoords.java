package net.walksy.client.modules.render;

import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.walksy.client.WalksyClient;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;

public class PearlCoords extends Module  {


    //TODO THIS

    public PearlCoords() {
        super("PearlCoords(WIP)");

        this.setDescription("Says coords in chat of the pearl you or other people threw");

        this.setCategory("Render");


    }


    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
    }

    private String pearlOwner;

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                if (WalksyClient.getClient().world.getEntities() instanceof EnderPearlEntity pearl) {
                    pearlOwner = pearl.getOwner().toString();

                }
                ItemStack stack = WalksyClient.getClient().player.getMainHandStack();
                if (stack.getItem() == Items.ENDER_PEARL) {
                    Vec3d startPos = WalksyClient.getClient().player.getCameraPosVec(1.0f);
                    Vec3d endPos = WalksyClient.getClient().crosshairTarget.getPos();
                    double motionX = endPos.x - startPos.x;
                    double motionY = endPos.y - startPos.y;
                    double motionZ = endPos.z - startPos.z;
                    double motion = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
                    int ticks = 0;
                    while (ticks < 100) {
                        double posY = startPos.y + motionY / motion * ticks - 0.5 * 0.04 * ticks * ticks;
                        double posX = startPos.x + motionX / motion * ticks;
                        double posZ = startPos.z + motionZ / motion * ticks;
                        if (WalksyClient.getClient().world.getBlockState(new BlockPos(posX, posY, posZ)).getMaterial().isSolid()) {
                            if (WalksyClient.getClient().options.useKey.isPressed()) {
                                posX = Math.round(posX);
                                posY = Math.round(posY);
                                posZ = Math.round(posZ);
                                ChatUtils.displayMessage(pearlOwner + "'s pearl " + "will land at: " + posX + " " + posY + " " + posZ);
                                break;
                            }
                        }
                        ticks++;
                    }
                }
            }
            break;
        }
    }
}




