package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.util.math.MatrixStack;

public class InGameHudRenderEvent extends Event {
    public MatrixStack mStack;
    public float tickDelta;
    public CallbackInfo ci;

    public InGameHudRenderEvent(MatrixStack mStack, float tickDelta, CallbackInfo ci) {
        this.mStack = mStack;
        this.tickDelta = tickDelta;
        this.ci = ci;
    }
}
