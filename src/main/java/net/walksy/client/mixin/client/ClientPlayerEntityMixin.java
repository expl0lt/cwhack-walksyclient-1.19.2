package net.walksy.client.mixin.client;

import com.mojang.authlib.GameProfile;

import net.walksy.client.WalksyClient;
import net.walksy.client.components.systems.Rotator;
import net.walksy.client.gui.impl.ClickGUIScreen;
import net.walksy.client.modules.hud.Watermark;
import net.walksy.client.modules.walksymodules.ZeroCrystalPlaceDelay;
import net.walksy.client.utils.Rotations;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.events.client.ClientTickMovementEvent;
import net.walksy.client.events.client.PlayerChatEvent;
import net.walksy.client.events.client.PlayerMoveEvent;
import net.walksy.client.events.packet.PostMovementPacketEvent;
import net.walksy.client.events.packet.PreMovementPacketEvent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Inject(at = @At("HEAD"), method="move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V")
    private void onMove(MovementType type, Vec3d offset, CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new PlayerMoveEvent(type, offset, ci));
    }

    @Inject(at = @At("HEAD"), method="sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", cancellable = true)
    private void onSendChatMessage(String message, Text preview, CallbackInfo ci) {
        // Handle commands etc.
        WalksyClient.getInstance().processChatPost(message, ci);

        WalksyClient.getInstance().emitter.triggerEvent(new PlayerChatEvent(message, ci));
    }


    @Inject(at = @At("HEAD"), method="sendMovementPackets()V", cancellable = true)
    private void beforeSendMovementPackets(CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new PreMovementPacketEvent(ci));
    }

    @Inject(at = @At("TAIL"), method="sendMovementPackets()V", cancellable = false)
    private void afterSendMovementPackets(CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new PostMovementPacketEvent(ci));
    }

    @Inject(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
            ordinal = 0), method = "tick()V")
    private void onTick(CallbackInfo ci) {
        EventManager.fire(new PlayerTickListener.PlayerTickEvent());
        WalksyClient.getInstance().emitter.triggerEvent(new ClientTickEvent(ci));
        Rotator.onPlayerTick();
        ClickGUIScreen.onTick();
        Rotations.onTick();
    }

    @Inject(at = @At("HEAD"), method="tickMovement()V", cancellable = false)
    private void onTickMovement(CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new ClientTickMovementEvent(ci));
    }
}