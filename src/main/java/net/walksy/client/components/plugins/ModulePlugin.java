package net.walksy.client.components.plugins;
import net.walksy.client.events.Event;
import net.walksy.client.modules.Module;

public interface ModulePlugin {
    public void addListeners(Module parentModule);
    public void removeListeners(Module parentModule);
    public boolean fireEvent(Event event);
}
