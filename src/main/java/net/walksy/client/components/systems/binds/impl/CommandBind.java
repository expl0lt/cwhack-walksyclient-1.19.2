package net.walksy.client.components.systems.binds.impl;

import net.walksy.client.WalksyClient;
import net.walksy.client.components.systems.binds.Bind;

public class CommandBind implements Bind {
    private String command;

    public CommandBind(String command) {
        this.command = command;
    }

    /**
     * Gets the command to execute.
     * @return The command to execute.
     */
    public String getCommand() {
        return command;
    }

    @Override
    public void fire() {
        String prefix = WalksyClient.getInstance().config.commandPrefix;
        String cmd = this.getCommand();

        // Make sure that the command starts with the delimiter.
        if (!cmd.startsWith(prefix)) {
            cmd = prefix + cmd;
        }

        // Execute the command
        WalksyClient.getInstance().commandHandler.handle(cmd);
    }

    @Override
    public String toString() {
        return "Run command: " + this.getCommand();
    }
}
