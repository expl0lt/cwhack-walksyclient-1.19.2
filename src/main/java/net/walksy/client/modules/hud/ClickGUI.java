package net.walksy.client.modules.hud;

import java.util.ArrayList;
import java.util.List;

import net.walksy.client.WalksyClient;
import net.walksy.client.commands.structures.Command;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.config.specials.Mode;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.gui.impl.ClickGUIScreen;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.ImGuiUtils;

public class ClickGUI extends Module {
    public static boolean video = false;
    public static boolean gradient = false;


    public ClickGUI() {
        super("ClickGUI");
        this.setDescription("A way of toggling your settings with a GUI (Currently WIP)");

        this.addSetting(new Setting("Bouncy", true) {{
            this.setDescription("Toggles the rendering of the bouncy felixes");
        }});

        this.addSetting(new Setting("Mode", new Mode("Video", "Gradient")));


        //this.addSetting(new Setting("GradientBackground", true) {{
        //    this.setDescription("Toggles the rendering the gradientBackround");
        //}});

        //START COLORS
        this.addSetting(new GradientTopBackground("GradientTopRed", 1) {{
            this.setMax(255);
            this.setMin(0);
        }});
        this.addSetting(new GradientTopBackground("GradientTopGreen", 1) {{
            this.setMax(255);
            this.setMin(0);
        }});
        this.addSetting(new GradientTopBackground("GradientTopBlue", 1) {{
            this.setMax(255);
            this.setMin(0);
        }});

        this.addSetting(new Opacity("TopOpacity", 1) {{
            this.setMax(255);
            this.setMin(0);
        }});

        //END COLORS

        this.addSetting(new GradientBottomBackground("GradientBottomRed", 1) {{
            this.setMax(255);
            this.setMin(0);
        }});
        this.addSetting(new GradientBottomBackground("GradientBottomGreen", 1) {{
            this.setMax(255);
            this.setMin(0);
        }});
        this.addSetting(new GradientBottomBackground("GradientBottomBlue", 1) {{
            this.setMax(255);
            this.setMin(0);
        }});

        this.addSetting(new Opacity("BottomOpacity", 1) {{
            this.setMax(255);
            this.setMin(0);
        }});



        this.addSetting(new Setting("TotalBouncies", 1) {{
            this.setDescription("The total amount of felixes bouncing around in the background!");
            this.setMax(420);
            this.setMin(1);
        }});

        this.addSetting(new Setting("BouncySpeed", 1.0d) {{
            this.setDescription("The speed of the felixes");

            this.setMax(50d);
            this.setMin(0d);
        }});

        this.addSetting(new Setting("Scale", 1.0d) {{
            this.setDescription("The scale of the GUI");
        }});

        this.setCategory("HUD");
    }

    ClickGUIScreen screen;

    @Override
    public void activate() {
        this.screen = new ClickGUIScreen();

        this.addListen(ClientTickEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
    }

    @Override
    public void fireEvent(Event event) {
        // TODO this is due to the fact that the activate command is called before the closeScreen function.
        // Basically, make it so that activate triggers after the chatscreen is closed.
        if (WalksyClient.getClient().currentScreen == null) {
            WalksyClient.getClient().setScreen(screen);
            
            ImGuiUtils.refreshStyle();
        }
    }

    @Override
    public Iterable<Command> getCommands() {
        List<Command> commands = new ArrayList<>();

        commands.add(new RefreshStyleCommand());
        commands.add(new ResetCommand());

        return commands;
    }

    public class RefreshStyleCommand extends Command {

        public RefreshStyleCommand() {
            super("refresh", "", "Refreshes the GUI style");
        }

        @Override
        public Boolean trigger(String[] args) {
            ImGuiUtils.refreshStyle();

            return true;
        }
    }

    public class ResetCommand extends Command {

        public ResetCommand() {
            super("reset", "", "Resets the GUI window position");
        }

        @Override
        public Boolean trigger(String[] args) {
            ClickGUIScreen.resetNext = true;

            return true;
        }
    }

    private class GradientTopBackground extends Setting {
        public GradientTopBackground(String name, Object value) {
            super(name, value);

            this.setCategory("GradientTopColors");
        }

        @Override
        public boolean shouldShow() {
            return getModeSetting("Mode").is("Gradient");
        }
    }

    private class GradientBottomBackground extends Setting {
        public GradientBottomBackground(String name, Object value) {
            super(name, value);

            this.setCategory("GradientBottomColors");
        }

        @Override
        public boolean shouldShow() {
            return getModeSetting("Mode").is("Gradient");
        }
    }

    private class Opacity extends Setting {
        public Opacity(String name, Object value) {
            super(name, value);

            this.setCategory("Opacity");
        }

        @Override
        public boolean shouldShow() {
            return getModeSetting("Mode").is("Gradient");
        }
    }
}
