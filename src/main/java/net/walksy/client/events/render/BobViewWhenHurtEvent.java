package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.util.math.MatrixStack;

public class BobViewWhenHurtEvent extends Event {
    public MatrixStack mStack;
    public float f;
    public CallbackInfo ci;

    public BobViewWhenHurtEvent(MatrixStack mStack, float f, CallbackInfo ci) {
        this.mStack = mStack;
        this.f = f;
        this.ci = ci;
    }
}
