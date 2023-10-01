package net.walksy.client.modules;

import net.walksy.client.WalksyClient;
import net.walksy.client.events.Event;

/**
 * Dummy module for making modules be used more as flags than actual modules.
 */
public class DummyModule extends Module {

    public DummyModule(String name, boolean autoEnable) {
        super(name, autoEnable);
    }

    public DummyModule(String name) {
        super(name);
    }

    @Override
    public final void fireEvent(Event event) {
        WalksyClient.log(this.getName() + " fired event " + event.getClass().getSimpleName() + " when it shouldn't have! This is a bug!");
    }

    // These can be overridden but by default should do nothing

    @Override
    public void activate() {
        return;
    }

    @Override
    public void deactivate() {
        return;
    }
    
}
