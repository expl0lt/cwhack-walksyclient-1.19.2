package net.walksy.client.events.packet;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;

public class OnGameStateChangeEvent extends Event {
    public GameStateChangeS2CPacket packet;
    public CallbackInfo ci;

    public OnGameStateChangeEvent(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        this.packet = packet;
        this.ci = ci;
    }
}
