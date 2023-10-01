package net.walksy.client.events.client;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class DisconnectEvent extends Event {
    public CallbackInfo ci;

    public DisconnectEvent(CallbackInfo ci) {
        this.ci = ci;
    }
}
