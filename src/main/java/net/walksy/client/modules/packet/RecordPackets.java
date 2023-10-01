package net.walksy.client.modules.packet;

import net.minecraft.network.message.SentMessage;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.walksy.client.WalksyClient;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.events.packet.SendPacketEvent;
import net.walksy.client.events.screen.DeathEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.ChatUtils;

public class RecordPackets extends Module {
    public RecordPackets() {
        super("RecordPackets");

        this.setDescription("Sends packets in chat");
        this.setCategory("Packet");
    }

    @Override
    public void activate() {
        this.addListen(SendPacketEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(SendPacketEvent.class);
    }

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "SendPacketEvent": {
                SendPacketEvent e = ((SendPacketEvent) event);
                ChatUtils.displayMessage("Packet sent: " + e.getPacket().getClass().getSimpleName());
                break;
            }
        }
    }
}
