package net.walksy.client.modules.walksymodules;

import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.modules.DummyModule;

public class MarlowOptimizer extends DummyModule {

    public static boolean enabled = false;

    public MarlowOptimizer() {
        super("MarlowOptimizer");

        this.setDescription("MarlowOptimizer");

        this.setCategory("Combat");

    }


    @Override
    public void activate() {
        enabled = true;
    }

    @Override
    public void deactivate() {
        enabled = false;
    }
}
