package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.particle.Particle;

public class AddParticleEvent extends Event {
    public CallbackInfoReturnable<Particle> ci;

    public AddParticleEvent(CallbackInfoReturnable<Particle> ci) {
        this.ci = ci;
    }
}
