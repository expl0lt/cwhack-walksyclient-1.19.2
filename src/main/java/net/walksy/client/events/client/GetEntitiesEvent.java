package net.walksy.client.events.client;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;

public class GetEntitiesEvent extends Event {
    public CallbackInfoReturnable<Iterable<Entity>> cir;
    
    public GetEntitiesEvent(CallbackInfoReturnable<Iterable<Entity>> cir) {
        this.cir = cir;
    }
}
