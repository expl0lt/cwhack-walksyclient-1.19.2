package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;

public class IsEntityGlowingEvent extends Event {
    public CallbackInfoReturnable<Boolean> cir;
    public Entity entity;

    public IsEntityGlowingEvent(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        this.entity = entity;
        this.cir = cir;
    }
}
