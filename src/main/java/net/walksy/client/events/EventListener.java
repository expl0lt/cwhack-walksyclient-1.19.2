package net.walksy.client.events;

import java.awt.*;

public interface EventListener {
    void addListen(Class <? extends Event> event);
    void removeListen(Class <? extends Event> event);

    public void fireEvent(Event event);
}
