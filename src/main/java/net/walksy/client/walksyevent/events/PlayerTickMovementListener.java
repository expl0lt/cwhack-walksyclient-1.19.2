package net.walksy.client.walksyevent.events;

import net.walksy.client.walksyevent.CancellableEvent;
import net.walksy.client.walksyevent.Listener;

import java.util.ArrayList;

public interface PlayerTickMovementListener extends Listener
{
    void onPlayerTickMovement(PlayerTickMovementEvent event);

    class PlayerTickMovementEvent extends CancellableEvent<PlayerTickMovementListener>
    {
        @Override
        public void fire(ArrayList<PlayerTickMovementListener> listeners)
        {
            for (PlayerTickMovementListener listener : listeners)
            {
                listener.onPlayerTickMovement(this);
                if (isCancelled())
                    return;
            }
        }

        @Override
        public Class<PlayerTickMovementListener> getListenerType()
        {
            return PlayerTickMovementListener.class;
        }
    }
}
