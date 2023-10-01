package net.walksy.client.modules.utilities;

import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.events.packet.OnWorldTimeUpdateEvent;
import net.walksy.client.events.packet.SendPacketEvent;
import net.walksy.client.events.render.InGameHudRenderEvent;
import net.walksy.client.misc.Colour;
import net.walksy.client.modules.Module;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class NoRespondAlert extends Module {
    public NoRespondAlert() {
        super("ServerLagMonitor");

        this.addSetting(new Setting("WarningTime", 1d));
        this.addSetting(new Setting("DisplayHeight", 150));

        this.addSetting(new Setting("ShowWhenClosed", true));
    
        this.setDescription("Displays an alert when the server has stopped sending data.");
        
        this.setCategory("Utility");
    }

    @Override
    public void activate() {
        this.addListen(OnWorldTimeUpdateEvent.class);
        this.addListen(SendPacketEvent.class);
        this.addListen(ClientTickEvent.class);
        this.addListen(InGameHudRenderEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(OnWorldTimeUpdateEvent.class);
        this.removeListen(ClientTickEvent.class);
        this.removeListen(InGameHudRenderEvent.class);
        this.removeListen(SendPacketEvent.class);
    }

    private double lastResp = -1;

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "InGameHudRenderEvent": {
                if (!WalksyClient.me().isAlive()) break;

                // Handle disconnection (i.e. when you block the ban packet)
                if (!this.getBoolSetting("ShowWhenClosed") && !WalksyClient.me().networkHandler.getConnection().isOpen()) break;

                float noRespTime = (float) (WalksyClient.getCurrentTime() - lastResp);
                if (noRespTime < (Double)this.getSetting("WarningTime").value) break;

                InGameHudRenderEvent e = (InGameHudRenderEvent)event;

                TextRenderer t = WalksyClient.getInstance().textRenderer;
                Text message = Text.of("Server not responded for ");
                Text timer = Text.of(String.format("%.2f", noRespTime));

                // Get the rendered width
                int width = t.getWidth(message) + t.getWidth(timer);
                
                // See where the user wants to put it on the screen
                int userHeight = (int)this.getSetting("DisplayHeight").value;

                int x = WalksyClient.getClient().getWindow().getScaledWidth()/2 - width/2;
                int y = (int)((double)userHeight/ WalksyClient.getClient().getWindow().getScaleFactor());

                // Timer colour thing
                float badDistance = 15f - noRespTime;
                badDistance = badDistance < 0 ? 0 : badDistance;

                // Get the colour
                Colour timerColour = Colour.fromDistance(badDistance);

                // Render the text
                x = t.drawWithShadow(e.mStack, message, x, y, 0xFFFFFFFF);
                x = t.drawWithShadow(e.mStack, timer, x, y, (new Colour((int)timerColour.r, (int)timerColour.g, (int)timerColour.b, 255).toARGB()));

                break;
            }
            case "OnWorldTimeUpdateEvent": {
                lastResp = WalksyClient.getCurrentTime();

                break;
            }
        }
    }
}
