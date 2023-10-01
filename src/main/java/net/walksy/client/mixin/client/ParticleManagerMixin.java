package net.walksy.client.mixin.client;

import java.util.Map;
import java.util.Queue;

import net.walksy.client.WalksyClient;
import net.walksy.client.interfaces.mixin.IParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.walksy.client.events.client.AddParticleEmitterEvent;
import net.walksy.client.events.render.AddParticleEvent;
import net.minecraft.client.particle.EmitterParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin implements IParticleManager {
    @Inject(at = @At("HEAD"), method = "addEmitter", cancellable = true)
    private void onAddEmitter(CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new AddParticleEmitterEvent(ci));
    }

    @Inject(at = @At("HEAD"), method = "addParticle", cancellable = true)
    private void onAddParticle(CallbackInfoReturnable<Particle> ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new AddParticleEvent(ci));
    }

    @Shadow private Queue<EmitterParticle> newEmitterParticles;
    @Shadow private Map<ParticleTextureSheet, Queue<Particle>> particles;

    @Override
    public void clearAll() {
        this.newEmitterParticles.clear();
        this.particles.clear();
    }
}
