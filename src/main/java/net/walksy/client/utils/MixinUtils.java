package net.walksy.client.utils;

import net.walksy.client.walksyevent.CancellableEvent;
import net.walksy.client.walksyevent.Event;
import net.walksy.client.walksyevent.EventManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public enum MixinUtils {
    ;

    public static void fireEvent(Event<?> event) {
        EventManager.fire(event);
    }

    public static void fireCancellable(CancellableEvent<?> event, CallbackInfo ci) {
        EventManager.fire(event);
        if (event.isCancelled() && ci.isCancellable())
            ci.cancel();
    }
}
