package net.walksy.client.mixin.client;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.walksy.client.events.io.OnKeyEvent;
import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(at = @At("HEAD"), method="onKey(JIIII)V", cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new OnKeyEvent(window, key, scancode, action, modifiers, ci));
    }
}
