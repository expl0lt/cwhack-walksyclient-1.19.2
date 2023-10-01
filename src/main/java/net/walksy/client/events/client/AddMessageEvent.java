package net.walksy.client.events.client;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.text.Text;

public class AddMessageEvent extends Event {
    public Text chatText;
    public CallbackInfo ci;

    public AddMessageEvent(Text chatText, CallbackInfo ci) {
        this.chatText = chatText;
        this.ci = ci;
    }
}
