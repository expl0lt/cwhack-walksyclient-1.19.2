package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;

public class GetModelEvent extends Event {
    public PlayerEntity player;
    public CallbackInfoReturnable<String> cir;

    public GetModelEvent(PlayerEntity player, CallbackInfoReturnable<String> cir) {
        this.player = player;
        this.cir = cir;
    }
}
