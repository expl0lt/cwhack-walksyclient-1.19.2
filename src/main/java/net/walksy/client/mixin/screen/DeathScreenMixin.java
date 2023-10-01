package net.walksy.client.mixin.screen;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.walksy.client.events.screen.DeathEvent;
import net.minecraft.client.gui.screen.DeathScreen;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {
    @Inject(at = @At("TAIL"), method = "init()V", cancellable = false)
    public void init(CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new DeathEvent(ci));
    }
}
