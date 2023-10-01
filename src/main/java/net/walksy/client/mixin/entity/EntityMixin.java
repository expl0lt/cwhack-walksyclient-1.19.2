package net.walksy.client.mixin.entity;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.walksy.client.events.render.IsEntityGlowingEvent;
import net.walksy.client.events.render.IsEntityInvisibleEvent;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("RETURN"), method="isInvisible()Z", cancellable = true)
    public void onIsInvisible(CallbackInfoReturnable<Boolean> cir) {
        WalksyClient.getInstance().emitter.triggerEvent(new IsEntityInvisibleEvent(cir));
    }

    @Inject(at = @At("RETURN"), method="isGlowing()Z", cancellable = true)
    public void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity ent = (Entity)((Object)(this));

        WalksyClient.getInstance().emitter.triggerEvent(new IsEntityGlowingEvent(ent, cir));
    }
}
