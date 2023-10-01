package net.walksy.client.modules.render;

import net.walksy.client.events.Event;
import net.walksy.client.events.render.GetRainGradientEvent;
import net.walksy.client.modules.Module;

public class NoWeather extends Module {
    public NoWeather() {
        super("AntiBritish");

        this.setDescription("Hides the rain.");
        this.setCategory("Render");
    }

    @Override
    public void activate() {
        this.addListen(GetRainGradientEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(GetRainGradientEvent.class);
    }

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "GetRainGradientEvent": {
                ((GetRainGradientEvent)event).cir.setReturnValue(0f);
                break;
            }
        }
    }
}
