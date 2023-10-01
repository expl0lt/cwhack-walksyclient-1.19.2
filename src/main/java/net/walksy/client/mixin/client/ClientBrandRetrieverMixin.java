package net.walksy.client.mixin.client;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.walksy.client.events.client.GetClientModNameEvent;
import net.minecraft.client.ClientBrandRetriever;

@Mixin(ClientBrandRetriever.class)
public class ClientBrandRetrieverMixin {
    @Inject(at = @At("TAIL"), method="getClientModName()Ljava/lang/String;", cancellable = true, remap = false)
    private static void onGetClientModName(CallbackInfoReturnable<String> cir) {
        WalksyClient.getInstance().emitter.triggerEvent(new GetClientModNameEvent(cir));
    }
}
