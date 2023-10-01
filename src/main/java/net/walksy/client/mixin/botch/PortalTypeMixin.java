package net.walksy.client.mixin.botch;

import net.walksy.client.WalksyClient;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class PortalTypeMixin {
    private Screen tempCurrentScreen;
    
    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(at = @At(value = "FIELD",
		target = "Lnet/minecraft/client/network/ClientPlayerEntity;nextNauseaStrength:F",
		opcode = Opcodes.GETFIELD,
		ordinal = 1), method = {"updateNausea()V"})
	private void afterUpdateNausea(CallbackInfo ci) {
		if(tempCurrentScreen == null)
			return;
		
		client.currentScreen = tempCurrentScreen;
		tempCurrentScreen    = null;
	}
}
