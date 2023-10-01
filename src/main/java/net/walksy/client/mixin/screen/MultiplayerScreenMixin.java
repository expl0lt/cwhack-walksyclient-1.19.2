package net.walksy.client.mixin.screen;

import net.walksy.client.WalksyClient;
import net.walksy.client.utils.ServerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.walksy.client.events.screen.ConnectEvent;
import net.minecraft.client.network.ServerInfo;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {

    @Inject(at = @At("HEAD"), method = "connect(Lnet/minecraft/client/network/ServerInfo;)V")
	private void onConnect(ServerInfo entry, CallbackInfo ci) {
        // For the util
        ServerUtils.setLastServer(entry);

        // For the event
		WalksyClient.getInstance().emitter.triggerEvent(new ConnectEvent(entry, ci));
        
	}
}
