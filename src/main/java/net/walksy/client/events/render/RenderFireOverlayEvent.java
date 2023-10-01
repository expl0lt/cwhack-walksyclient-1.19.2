package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class RenderFireOverlayEvent extends Event {
    public MinecraftClient client;
    public MatrixStack mStack;
    public CallbackInfo ci;

    public RenderFireOverlayEvent(MinecraftClient client, MatrixStack mStack, CallbackInfo ci) {
        this.client = client;
        this.mStack = mStack;
        this.ci = ci;
    }
}
