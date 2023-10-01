package net.walksy.client.mixin.entity;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.walksy.client.events.client.JumpEvent;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(at = @At("HEAD"), method="jump()V", cancellable = true)
    private void onJump(CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new JumpEvent(ci));
    }
}
