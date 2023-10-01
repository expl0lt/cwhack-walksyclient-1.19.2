package net.walksy.client.utils;

import java.util.List;

import net.walksy.client.WalksyClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class ServerUtils {
    private static ServerInfo lastServer;

    public static ServerInfo getLastServer() {
        return lastServer;
    }

    public static void setLastServer(ServerInfo last) {
        lastServer = last;
    }

    public static void connectToServer(ServerInfo info, Screen prevScreen) {
        if (info == null) return;

        ConnectScreen.connect(prevScreen, WalksyClient.getClient(), ServerAddress.parse(info.address), info);
    }

    public static AbstractClientPlayerEntity getPlayerByName(String name) {
        List<AbstractClientPlayerEntity> players = WalksyClient.getClient().world.getPlayers();

        // TODO use a tab list since this isn't all players, just rendered ones!
        for (AbstractClientPlayerEntity player : players) {
            if (player.getDisplayName().getContent().equals(name)) {
                return player;
            }
        }

        return null;
    }

    public static Integer getTotalOnlinePlayers() {
        if (WalksyClient.getClient().world == null) return -1;

        return WalksyClient.getClient().world.getPlayers().size();
    }

    // Doesn't always work super well
    public static Integer getTotalPlayerSlots() {
        Integer fakeTotal = getTotalOnlinePlayers() + 1;

        if (getLastServer() == null) return fakeTotal;

        // This can occur when you connect from something that doesn't always have a label
        Text playerCountLabel = getLastServer().playerCountLabel;
        if (playerCountLabel == null) return fakeTotal;

        // Get the different parts of the player label
        List<Text> parts = playerCountLabel.getSiblings();
        if (parts.size() != 2) return fakeTotal;

        return Integer.decode(parts.get(1).getString());
    }
}
