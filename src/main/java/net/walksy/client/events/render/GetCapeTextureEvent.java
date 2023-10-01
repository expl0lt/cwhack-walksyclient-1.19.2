package net.walksy.client.events.render;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class GetCapeTextureEvent extends Event {
    public PlayerEntity player;
    public CallbackInfoReturnable<Identifier> cir;

    public GetCapeTextureEvent(PlayerEntity player, CallbackInfoReturnable<Identifier> cir) {
        this.player = player;
        this.cir = cir;
    }
}
