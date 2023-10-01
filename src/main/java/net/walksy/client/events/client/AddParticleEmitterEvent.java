package net.walksy.client.events.client;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class AddParticleEmitterEvent extends Event {
    public CallbackInfo ci;

    public AddParticleEmitterEvent(CallbackInfo ci) {
        this.ci = ci;
    }
}
