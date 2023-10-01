package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class GetRainGradientEvent extends Event {
    public CallbackInfoReturnable<Float> cir;
    public float delta;

    public GetRainGradientEvent(float delta, CallbackInfoReturnable<Float> cir) {
        this.delta = delta;
        this.cir = cir;
    }
}
