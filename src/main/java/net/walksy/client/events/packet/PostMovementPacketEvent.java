package net.walksy.client.events.packet;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PostMovementPacketEvent extends Event {
    public CallbackInfo ci;

    public PostMovementPacketEvent(CallbackInfo ci) {
        this.ci = ci;
    }
}
