package net.walksy.client.commands;

import net.walksy.client.WalksyClient;
import net.walksy.client.commands.structures.Command;

public class FontCommand extends Command {

    public FontCommand() {
        super("font", "font <id | default: como-client:como, alternative: minecraft:default>", "Allows the user to specify a font to be used by the client (e.g. minecraft:font)");
    
        this.commandDisplay = "FontManager";
    }

    @Override
    public boolean shouldShowHelp(String[] args) {
        return args.length == 0;
    }

    @Override
    public Boolean trigger(String[] args) {
        if (handleHelp(args)) return true;

        // TODO make the font have persistance.
        String fontName = this.combineArgs(args);
        WalksyClient.getInstance().updateFont(fontName);

        // TODO update this somewhere else but like add persistance first.
        WalksyClient.getInstance().config.font = fontName;

        this.displayChatMessage(String.format("Changed font to '%s'", fontName));

        return true;
    }
}
