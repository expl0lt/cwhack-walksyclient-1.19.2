package net.walksy.client.commands;

import net.walksy.client.WalksyClient;
import net.walksy.client.commands.structures.Command;
import net.walksy.client.utils.ChatUtils;
import net.walksy.client.utils.ServerUtils;
import net.minecraft.client.network.ServerInfo;

public class CopyServerIPCommand extends Command {
    public CopyServerIPCommand() {
        super("copyip", "<no args>", "copies the last server's address to the clipboard");
    }

    @Override
    public Boolean trigger(String[] args) {
        ServerInfo lastServer = ServerUtils.getLastServer();

        if (lastServer == null) {
            this.displayChatMessage(
                String.format("%sUnable to find server.", ChatUtils.RED)
            );

            return true;
        }

        // Get server address and print it
        String ip = lastServer.address;
        this.displayChatMessage(String.format("Copied server address to clipboard: %s%s", ChatUtils.GREEN, ip));

        // Set the clipboard
        WalksyClient.getClient().keyboard.setClipboard(ip);

        return true;
    }
}
