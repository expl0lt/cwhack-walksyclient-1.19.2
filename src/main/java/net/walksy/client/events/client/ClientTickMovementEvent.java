package net.walksy.client.events.client;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.walksy.client.events.Event;

public class ClientTickMovementEvent extends Event {
    public CallbackInfo ci;
    
    public ClientTickMovementEvent(CallbackInfo ci) {
        this.ci = ci;
    }
}
