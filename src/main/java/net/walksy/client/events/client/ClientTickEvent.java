package net.walksy.client.events.client;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.walksy.client.events.Event;

public class ClientTickEvent extends Event {
    public CallbackInfo ci;

    public ClientTickEvent(CallbackInfo ci) {
        this.ci = ci;
    }
}
