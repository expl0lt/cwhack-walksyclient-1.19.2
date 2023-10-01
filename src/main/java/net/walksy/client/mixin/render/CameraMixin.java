package net.walksy.client.mixin.render;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.walksy.client.events.render.OnSubmersionTypeEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;

@Mixin(Camera.class)
public class CameraMixin {    
    @Inject(at = @At("RETURN"), method="getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;", cancellable = true)
    public void onGetSubmersionType(CallbackInfoReturnable<CameraSubmersionType> cir) {
        WalksyClient.getInstance().emitter.triggerEvent(new OnSubmersionTypeEvent(cir));
    }
}