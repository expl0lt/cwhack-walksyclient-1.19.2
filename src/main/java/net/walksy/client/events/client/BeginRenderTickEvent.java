package net.walksy.client.events.client;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.walksy.client.events.Event;

public class BeginRenderTickEvent extends Event {
    public long timeMillis;
    public CallbackInfoReturnable<Integer> ci;

    public BeginRenderTickEvent(long timeMillis, CallbackInfoReturnable<Integer> ci) {
        this.timeMillis = timeMillis;
        this.ci = ci;
    }
}
