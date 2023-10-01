package net.walksy.client.modules.utilities;

import net.walksy.client.WalksyClient;
import net.walksy.client.events.Event;
import net.walksy.client.events.screen.DeathEvent;
import net.walksy.client.modules.Module;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn");

        this.setDescription("Automatically respawns the player.");
        this.setCategory("Utility");
    }

    @Override
    public void activate() {
        this.addListen(DeathEvent.class);;
    }

    @Override
    public void deactivate() {
        this.removeListen(DeathEvent.class);
    }
    
    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "DeathEvent": {
                WalksyClient.me().requestRespawn();
                break;
            }
        }
    }
}
