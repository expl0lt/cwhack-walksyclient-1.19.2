package net.walksy.client.events.client;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class OnClientCloseEvent extends Event {
    CallbackInfo ci;
    
    public OnClientCloseEvent(CallbackInfo ci) {
        this.ci = ci;
    }
}
