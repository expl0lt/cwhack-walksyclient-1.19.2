package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.CameraSubmersionType;

public class OnSubmersionTypeEvent extends Event {
    public CallbackInfoReturnable<CameraSubmersionType> cir;

    public OnSubmersionTypeEvent(CallbackInfoReturnable<CameraSubmersionType> cir) {
        this.cir = cir;
    }
}
