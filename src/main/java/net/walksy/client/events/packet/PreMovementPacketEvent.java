package net.walksy.client.events.packet;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PreMovementPacketEvent extends Event {
    public CallbackInfo ci;

    public PreMovementPacketEvent(CallbackInfo ci) {
        this.ci = ci;
    }
}
