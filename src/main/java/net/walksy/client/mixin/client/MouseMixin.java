package net.walksy.client.mixin.client;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.walksy.client.events.io.OnMouseButtonEvent;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(at = @At("HEAD"), method="onMouseButton(JIII)V", cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new OnMouseButtonEvent(window, button, action, mods, ci));
    }
}
