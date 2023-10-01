package net.walksy.client.modules.render;

import net.walksy.client.events.Event;
import net.walksy.client.events.render.BobViewWhenHurtEvent;
import net.walksy.client.modules.Module;

public class NoHurtCam extends Module {
    public NoHurtCam() {
        super("NoHurtCam");

        this.setDescription("Disables the screen rotation when getting damaged.");
        this.setCategory("Render");
    }

    @Override
    public void activate() {
        this.addListen(BobViewWhenHurtEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(BobViewWhenHurtEvent.class);
    }

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "BobViewWhenHurtEvent": {
                ((BobViewWhenHurtEvent)event).ci.cancel();
                break;
            }
        }
    }
}
