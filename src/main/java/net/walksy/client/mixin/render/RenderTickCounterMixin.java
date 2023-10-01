package net.walksy.client.mixin.render;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.walksy.client.events.client.BeginRenderTickEvent;
import net.minecraft.client.render.RenderTickCounter;

@Mixin(RenderTickCounter.class)
public class RenderTickCounterMixin {
    @Inject(at = @At("HEAD"), method = "beginRenderTick(J)I", cancellable = true)
    private void onBeginRenderTick(long timeMillis, CallbackInfoReturnable<Integer> ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new BeginRenderTickEvent(timeMillis, ci));
    }
}
