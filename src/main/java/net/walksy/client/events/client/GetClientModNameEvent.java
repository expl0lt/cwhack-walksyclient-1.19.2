package net.walksy.client.events.client;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.walksy.client.events.Event;

public class GetClientModNameEvent extends Event {
    public CallbackInfoReturnable<String> cir;

    public GetClientModNameEvent(CallbackInfoReturnable<String> cir) {
        this.cir = cir;
    }
}
