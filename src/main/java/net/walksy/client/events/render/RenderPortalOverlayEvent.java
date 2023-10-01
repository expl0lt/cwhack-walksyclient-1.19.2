package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class RenderPortalOverlayEvent extends Event {
    public float nauseaStrength;
    public CallbackInfo ci;

    public RenderPortalOverlayEvent(float nauseaStrength, CallbackInfo ci) {
        this.nauseaStrength = nauseaStrength;
        this.ci = ci;
    }
}
