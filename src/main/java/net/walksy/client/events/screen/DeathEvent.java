package net.walksy.client.events.screen;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class DeathEvent extends Event {
    public CallbackInfo ci;

    public DeathEvent(CallbackInfo ci) {
        this.ci = ci;
    }
}
