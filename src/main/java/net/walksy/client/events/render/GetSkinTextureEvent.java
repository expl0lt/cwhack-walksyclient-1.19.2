package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class GetSkinTextureEvent extends Event {
    public PlayerEntity player;
    public CallbackInfoReturnable<Identifier> cir;

    public GetSkinTextureEvent(PlayerEntity player, CallbackInfoReturnable<Identifier> cir) {
        this.player = player;
        this.cir = cir;
    }
}
