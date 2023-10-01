package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BossBarHudRenderEvent extends Event {
    public CallbackInfo ci;

    public BossBarHudRenderEvent(CallbackInfo ci) {
        this.ci = ci;
    }
}
