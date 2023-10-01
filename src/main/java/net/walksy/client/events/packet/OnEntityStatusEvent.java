package net.walksy.client.events.packet;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class OnEntityStatusEvent extends Event {
    public CallbackInfo ci;
    public EntityStatusS2CPacket packet;

    public OnEntityStatusEvent(EntityStatusS2CPacket packet, CallbackInfo ci) {
        this.packet = packet;
        this.ci = ci;
    }
}
