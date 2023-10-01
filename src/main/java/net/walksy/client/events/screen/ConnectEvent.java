package net.walksy.client.events.screen;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ServerInfo;

public class ConnectEvent extends Event {
    public ServerInfo entry;
    public CallbackInfo ci;
    
    public ConnectEvent(ServerInfo entry, CallbackInfo ci) {
        this.entry = entry;
        this.ci = ci;
    }
}
