package net.walksy.client.modules.packet;

import net.walksy.client.WalksyClient;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.events.client.HandleDisconnectionEvent;
import net.walksy.client.events.client.OnDisconnectedEvent;
import net.walksy.client.events.render.RenderWorldEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.ChatUtils;
import net.walksy.client.utils.ClientUtils;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.text.Text;

public class AntiKick extends Module {
    public AntiKick() {
        super("AntiKick");

        this.setDescription("Blocks packets that cause you to disconnect from the server.");

        this.setCategory("Packet");
    }

    @Override
    public String listOption() {
        return this.wasKicked ? "Disconnected" : "Connected";
    }

    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
        this.addListen(OnDisconnectedEvent.class);
        this.addListen(HandleDisconnectionEvent.class);

        this.addListen(RenderWorldEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(OnDisconnectedEvent.class);
        this.removeListen(HandleDisconnectionEvent.class);
        this.removeListen(ClientTickEvent.class);

        this.removeListen(RenderWorldEvent.class);
    }

    private void displayReason(String reason) {
        if (reason == null || reason.isBlank()) {
            this.displayMessage("You were disconnected from the server!");
            return;
        }

        this.displayMessage(String.format("You were disconnected from the server: %s%s", ChatUtils.RED, reason));
    }

    private void displayReason(Text text) {
        this.displayReason(ClientUtils.getTextString(text));
    }

    private Boolean wasKicked = false;
    private Double lastWorld = 0d;

    private Boolean isInWorld() {
        // Make sure that the last render time was within the last 500ms
        Double d = WalksyClient.getCurrentTime() - lastWorld;

        // Make sure that we are not "downloading the terrain"
        return (d < 0.5d && !(WalksyClient.getClient().currentScreen instanceof DownloadingTerrainScreen));
    }

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "RenderWorldEvent": {
                RenderWorldEvent e = (RenderWorldEvent)event;
                
                this.lastWorld = WalksyClient.getCurrentTime();

                break;
            }
            
            case "ClientTickEvent": {
                if (WalksyClient.me().networkHandler.getConnection().isOpen() && this.wasKicked) {
                    this.wasKicked = false;
                }
                
                break;
            }

            case "OnDisconnectedEvent": {
                OnDisconnectedEvent e = (OnDisconnectedEvent)event;
                if (!this.isInWorld()) return;

                // Stop the disconnect.
                e.ci.cancel();

                // Display reason
                this.displayReason(e.reason);

                this.wasKicked = true;
                
                break;
            }

            case "HandleDisconnectionEvent": {
                HandleDisconnectionEvent e = (HandleDisconnectionEvent)event;
                if (!this.isInWorld()) return;

                if (WalksyClient.me() == null) break;

                // Prevent it (this will stop the handleDisconnect "being called twice" message)
                if (this.wasKicked) e.ci.cancel();
                
                break;
            }
        }
    }
}
