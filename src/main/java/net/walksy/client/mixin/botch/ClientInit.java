package net.walksy.client.mixin.botch;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class ClientInit {
    @Inject(at = @At("TAIL"), method = "<init>", cancellable = false)
    public void init(CallbackInfo ci) {
        WalksyClient.getInstance().initialise();
    }
}
