package net.walksy.client.gui.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.walksy.client.WalksyClient;
import net.walksy.client.components.systems.binds.Bind;
import net.walksy.client.components.systems.binds.BindsSystem;
import net.walksy.client.components.systems.binds.impl.ModuleBind;
import net.walksy.client.gui.ImGuiScreen;
import net.walksy.client.gui.impl.widgets.BouncyWidget;
import net.walksy.client.misc.Colour;
import net.walksy.client.utils.Color;
import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImDouble;
import imgui.type.ImInt;
import imgui.type.ImString;
import joptsimple.internal.Strings;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.config.specials.Mode;
import net.walksy.client.modules.Module;
import net.walksy.client.modules.hud.ClickGUI;
import net.walksy.client.modules.utilities.Binds;
import net.walksy.client.utils.ChatUtils;
import net.walksy.client.utils.ClientUtils;
import net.walksy.client.utils.ImGuiUtils;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;

public class ClickGUIScreen extends ImGuiScreen {
    private HashMap<String, List<Module>> categories = new HashMap<>();
    private List<BouncyWidget> bouncyWidgets = new ArrayList<>();
    private String searchExample;

    public static final Identifier[] frames =
            {new Identifier("walksy-client", "gui/frames/001.png"),
                    new Identifier("walksy-client", "gui/frames/002.png"),
                    new Identifier("walksy-client", "gui/frames/003.png"),
                    new Identifier("walksy-client", "gui/frames/004.png"),
                    new Identifier("walksy-client", "gui/frames/005.png"),
                    new Identifier("walksy-client", "gui/frames/006.png"),
                    new Identifier("walksy-client", "gui/frames/007.png"),
                    new Identifier("walksy-client", "gui/frames/008.png"),
                    new Identifier("walksy-client", "gui/frames/009.png"),
                    new Identifier("walksy-client", "gui/frames/010.png"),
                    new Identifier("walksy-client", "gui/frames/011.png"),
                    new Identifier("walksy-client", "gui/frames/012.png"),
                    new Identifier("walksy-client", "gui/frames/013.png"),
                    new Identifier("walksy-client", "gui/frames/014.png"),
                    new Identifier("walksy-client", "gui/frames/015.png"),
                    new Identifier("walksy-client", "gui/frames/016.png"),
                    new Identifier("walksy-client", "gui/frames/017.png"),
                    new Identifier("walksy-client", "gui/frames/018.png"),
                    new Identifier("walksy-client", "gui/frames/019.png"),
                    new Identifier("walksy-client", "gui/frames/020.png"),
                    new Identifier("walksy-client", "gui/frames/021.png"),
                    new Identifier("walksy-client", "gui/frames/022.png"),
                    new Identifier("walksy-client", "gui/frames/023.png"),
                    new Identifier("walksy-client", "gui/frames/024.png"),
                    new Identifier("walksy-client", "gui/frames/025.png"),
                    new Identifier("walksy-client", "gui/frames/026.png"),
                    new Identifier("walksy-client", "gui/frames/027.png"),
                    new Identifier("walksy-client", "gui/frames/028.png"),
                    new Identifier("walksy-client", "gui/frames/029.png"),
                    new Identifier("walksy-client", "gui/frames/030.png"),
                    new Identifier("walksy-client", "gui/frames/031.png"),
                    new Identifier("walksy-client", "gui/frames/032.png"),
                    new Identifier("walksy-client", "gui/frames/033.png"),
                    new Identifier("walksy-client", "gui/frames/034.png"),
                    new Identifier("walksy-client", "gui/frames/035.png"),
                    new Identifier("walksy-client", "gui/frames/036.png"),
                    new Identifier("walksy-client", "gui/frames/037.png"),
                    new Identifier("walksy-client", "gui/frames/038.png"),
                    new Identifier("walksy-client", "gui/frames/039.png"),
                    new Identifier("walksy-client", "gui/frames/040.png"),
                    new Identifier("walksy-client", "gui/frames/041.png"),
                    new Identifier("walksy-client", "gui/frames/042.png"),
                    new Identifier("walksy-client", "gui/frames/043.png"),
                    new Identifier("walksy-client", "gui/frames/044.png"),
                    new Identifier("walksy-client", "gui/frames/045.png"),
                    new Identifier("walksy-client", "gui/frames/046.png"),
                    new Identifier("walksy-client", "gui/frames/047.png"),
                    new Identifier("walksy-client", "gui/frames/048.png"),
                    new Identifier("walksy-client", "gui/frames/049.png"),
                    new Identifier("walksy-client", "gui/frames/050.png"),
                    new Identifier("walksy-client", "gui/frames/051.png"),
                    new Identifier("walksy-client", "gui/frames/052.png"),
                    new Identifier("walksy-client", "gui/frames/053.png"),
                    new Identifier("walksy-client", "gui/frames/054.png"),
                    new Identifier("walksy-client", "gui/frames/055.png"),
                    new Identifier("walksy-client", "gui/frames/056.png"),
                    new Identifier("walksy-client", "gui/frames/057.png"),
                    new Identifier("walksy-client", "gui/frames/058.png"),
                    new Identifier("walksy-client", "gui/frames/059.png"),
                    new Identifier("walksy-client", "gui/frames/061.png"),
                    new Identifier("walksy-client", "gui/frames/062.png"),
                    new Identifier("walksy-client", "gui/frames/063.png"),
                    new Identifier("walksy-client", "gui/frames/064.png"),
                    new Identifier("walksy-client", "gui/frames/065.png"),
                    new Identifier("walksy-client", "gui/frames/066.png"),
                    new Identifier("walksy-client", "gui/frames/067.png"),
                    new Identifier("walksy-client", "gui/frames/068.png"),
                    new Identifier("walksy-client", "gui/frames/069.png"),
                    new Identifier("walksy-client", "gui/frames/070.png"),
                    new Identifier("walksy-client", "gui/frames/071.png"),
                    new Identifier("walksy-client", "gui/frames/072.png"),
                    new Identifier("walksy-client", "gui/frames/073.png"),
                    new Identifier("walksy-client", "gui/frames/074.png"),
                    new Identifier("walksy-client", "gui/frames/075.png"),
                    new Identifier("walksy-client", "gui/frames/076.png"),
                    new Identifier("walksy-client", "gui/frames/077.png"),
                    new Identifier("walksy-client", "gui/frames/078.png"),
                    new Identifier("walksy-client", "gui/frames/079.png"),
                    new Identifier("walksy-client", "gui/frames/080.png"),
                    new Identifier("walksy-client", "gui/frames/081.png"),
                    new Identifier("walksy-client", "gui/frames/082.png"),
                    new Identifier("walksy-client", "gui/frames/083.png"),
                    new Identifier("walksy-client", "gui/frames/084.png"),
                    new Identifier("walksy-client", "gui/frames/085.png"),
                    new Identifier("walksy-client", "gui/frames/086.png"),
                    new Identifier("walksy-client", "gui/frames/087.png"),
                    new Identifier("walksy-client", "gui/frames/088.png"),
                    new Identifier("walksy-client", "gui/frames/089.png"),
                    new Identifier("walksy-client", "gui/frames/090.png"),
                    new Identifier("walksy-client", "gui/frames/091.png"),
                    new Identifier("walksy-client", "gui/frames/092.png"),
                    new Identifier("walksy-client", "gui/frames/093.png"),
                    new Identifier("walksy-client", "gui/frames/094.png"),
                    new Identifier("walksy-client", "gui/frames/095.png"),
                    new Identifier("walksy-client", "gui/frames/096.png"),
                    new Identifier("walksy-client", "gui/frames/097.png"),
                    new Identifier("walksy-client", "gui/frames/098.png"),
                    new Identifier("walksy-client", "gui/frames/099.png"),
                    new Identifier("walksy-client", "gui/frames/100.png"),
                    new Identifier("walksy-client", "gui/frames/101.png"),
                    new Identifier("walksy-client", "gui/frames/102.png"),
                    new Identifier("walksy-client", "gui/frames/103.png"),
                    new Identifier("walksy-client", "gui/frames/104.png"),
                    new Identifier("walksy-client", "gui/frames/105.png"),
                    new Identifier("walksy-client", "gui/frames/106.png"),
                    new Identifier("walksy-client", "gui/frames/107.png"),
                    new Identifier("walksy-client", "gui/frames/108.png"),
                    new Identifier("walksy-client", "gui/frames/109.png"),
                    new Identifier("walksy-client", "gui/frames/110.png"),
                    new Identifier("walksy-client", "gui/frames/111.png"),
                    new Identifier("walksy-client", "gui/frames/112.png"),
                    new Identifier("walksy-client", "gui/frames/113.png"),
                    new Identifier("walksy-client", "gui/frames/114.png"),
                    new Identifier("walksy-client", "gui/frames/115.png"),
                    new Identifier("walksy-client", "gui/frames/116.png"),
                    new Identifier("walksy-client", "gui/frames/117.png"),
                    new Identifier("walksy-client", "gui/frames/118.png"),
                    new Identifier("walksy-client", "gui/frames/119.png"),
                    new Identifier("walksy-client", "gui/frames/120.png"),
                    new Identifier("walksy-client", "gui/frames/121.png"),
                    new Identifier("walksy-client", "gui/frames/122.png"),
                    new Identifier("walksy-client", "gui/frames/123.png"),
                    new Identifier("walksy-client", "gui/frames/124.png"),
                    new Identifier("walksy-client", "gui/frames/125.png"),
                    new Identifier("walksy-client", "gui/frames/126.png"),
                    new Identifier("walksy-client", "gui/frames/127.png"),
                    new Identifier("walksy-client", "gui/frames/128.png"),
                    new Identifier("walksy-client", "gui/frames/129.png"),
                    new Identifier("walksy-client", "gui/frames/130.png"),
                    new Identifier("walksy-client", "gui/frames/131.png"),
                    new Identifier("walksy-client", "gui/frames/132.png"),
                    new Identifier("walksy-client", "gui/frames/133.png"),
                    new Identifier("walksy-client", "gui/frames/134.png"),
                    new Identifier("walksy-client", "gui/frames/135.png"),
                    new Identifier("walksy-client", "gui/frames/136.png"),
                    new Identifier("walksy-client", "gui/frames/137.png"),
                    new Identifier("walksy-client", "gui/frames/138.png"),
                    new Identifier("walksy-client", "gui/frames/139.png"),
                    new Identifier("walksy-client", "gui/frames/140.png"),
                    new Identifier("walksy-client", "gui/frames/141.png"),
                    new Identifier("walksy-client", "gui/frames/142.png"),
                    new Identifier("walksy-client", "gui/frames/143.png"),
                    new Identifier("walksy-client", "gui/frames/144.png"),
                    new Identifier("walksy-client", "gui/frames/145.png"),
                    new Identifier("walksy-client", "gui/frames/146.png"),
                    new Identifier("walksy-client", "gui/frames/147.png"),
                    new Identifier("walksy-client", "gui/frames/148.png"),
                    new Identifier("walksy-client", "gui/frames/149.png"),
                    new Identifier("walksy-client", "gui/frames/150.png"),
                    new Identifier("walksy-client", "gui/frames/151.png"),
                    new Identifier("walksy-client", "gui/frames/152.png"),
                    new Identifier("walksy-client", "gui/frames/153.png"),
                    new Identifier("walksy-client", "gui/frames/154.png"),
                    new Identifier("walksy-client", "gui/frames/155.png"),
                    new Identifier("walksy-client", "gui/frames/156.png"),
                    new Identifier("walksy-client", "gui/frames/157.png"),
                    new Identifier("walksy-client", "gui/frames/158.png"),
                    new Identifier("walksy-client", "gui/frames/159.png"),
                    new Identifier("walksy-client", "gui/frames/160.png"),
                    new Identifier("walksy-client", "gui/frames/161.png"),
                    new Identifier("walksy-client", "gui/frames/162.png"),
                    new Identifier("walksy-client", "gui/frames/163.png"),
                    new Identifier("walksy-client", "gui/frames/164.png"),
                    new Identifier("walksy-client", "gui/frames/165.png"),
                    new Identifier("walksy-client", "gui/frames/166.png"),
                    new Identifier("walksy-client", "gui/frames/167.png"),
                    new Identifier("walksy-client", "gui/frames/168.png"),
                    new Identifier("walksy-client", "gui/frames/169.png"),
                    new Identifier("walksy-client", "gui/frames/170.png"),
                    new Identifier("walksy-client", "gui/frames/171.png"),
                    new Identifier("walksy-client", "gui/frames/172.png"),
                    new Identifier("walksy-client", "gui/frames/173.png"),
                    new Identifier("walksy-client", "gui/frames/174.png"),
                    new Identifier("walksy-client", "gui/frames/175.png"),
                    new Identifier("walksy-client", "gui/frames/176.png"),
                    new Identifier("walksy-client", "gui/frames/177.png"),
                    new Identifier("walksy-client", "gui/frames/178.png"),
                    new Identifier("walksy-client", "gui/frames/179.png"),
                    new Identifier("walksy-client", "gui/frames/180.png"),
                    new Identifier("walksy-client", "gui/frames/181.png"),
                    new Identifier("walksy-client", "gui/frames/182.png"),
                    new Identifier("walksy-client", "gui/frames/183.png"),
                    new Identifier("walksy-client", "gui/frames/184.png"),
                    new Identifier("walksy-client", "gui/frames/185.png"),
                    new Identifier("walksy-client", "gui/frames/186.png"),
                    new Identifier("walksy-client", "gui/frames/187.png"),
                    new Identifier("walksy-client", "gui/frames/188.png"),
                    new Identifier("walksy-client", "gui/frames/189.png"),
                    new Identifier("walksy-client", "gui/frames/190.png"),
                    new Identifier("walksy-client", "gui/frames/191.png"),
                    new Identifier("walksy-client", "gui/frames/192.png"),
                    new Identifier("walksy-client", "gui/frames/193.png"),
                    new Identifier("walksy-client", "gui/frames/194.png"),
                    new Identifier("walksy-client", "gui/frames/195.png"),
                    new Identifier("walksy-client", "gui/frames/196.png"),
                    new Identifier("walksy-client", "gui/frames/197.png"),
                    new Identifier("walksy-client", "gui/frames/198.png"),
                    new Identifier("walksy-client", "gui/frames/199.png"),
                    new Identifier("walksy-client", "gui/frames/200.png"),
                    new Identifier("walksy-client", "gui/frames/001.png"),
                    new Identifier("walksy-client", "gui/frames/002.png"),
                    new Identifier("walksy-client", "gui/frames/003.png"),
                    new Identifier("walksy-client", "gui/frames/004.png"),


            };






    private double getBouncySpeed() {
        return this.getClickGUI().getDoubleSetting("BouncySpeed");
    }


    private void syncBouncySettings() {
        // Check if the bouncies are enabled.
        if (!this.getClickGUI().getBoolSetting("Bouncy")) {
            this.bouncyWidgets.clear();
            return;
        }

        // Check the total amount of felixes.
        int totalFelixes = this.getClickGUI().getIntSetting("TotalBouncies");

        // Get the bouncy speed.
        double bouncySpeed = this.getBouncySpeed();

        // Make sure that they're the same amount.
        while (this.bouncyWidgets.size() < totalFelixes) {
            // Add a new felix.
            this.bouncyWidgets.add(new BouncyWidget(this.width, this.height, 1d/6, bouncySpeed));
        }

        while (this.bouncyWidgets.size() > totalFelixes) {
            // Remove a felix.
            this.bouncyWidgets.remove(this.bouncyWidgets.size() - 1);
        }

        // Sync the felixes.
        for (BouncyWidget felix : this.bouncyWidgets) {
            felix.setSpeed(bouncySpeed);
        }
    }

    @Override
    protected void init() {
        super.init();

        // Clear the current components
        categories.clear();
        bouncyWidgets.clear();

        // Add all modules to the categories
        for (Module mod : WalksyClient.getInstance().getModules().values()) {
            String cat = mod.getCategory();

            categories.putIfAbsent(cat, new ArrayList<Module>());
            categories.get(cat).add(mod);
        }

        // Sync the bouncy settings
        this.syncBouncySettings();

        // Set the search example as random module name
        this.updateSearchExample();

        // Update the keybinds
        this.populateKeyBinds();
    }

    private void updateSearchExample() {
        // Get a random index
        int i = (int)(Math.random() * WalksyClient.getInstance().getModules().size());

        // Iterate and get the module
        for (Module mod : WalksyClient.getInstance().getModules().values()) {
            if (i-- == 0) {
                this.searchExample = mod.getName();
                return;
            }
        }

        // Just return an empty string
        this.searchExample = Strings.EMPTY;
    }

    private static HashMap<Module, Boolean> openedOptions = new HashMap<>();

    /**
     * States if the settings should be rendered for a given module
     * @return if the settings should be rendered
     */
    public boolean shouldShowOptions(Module module) {
        return openedOptions.containsKey(module) && openedOptions.get(module);
    }

    /**
     * Hides the options for the given module
     * @param module
     */
    public void hideOptions(Module module) {
        // Remove the module from the opened settings hashmap
        openedOptions.remove(module);
    }

    /**
     * Flags a given module to have its options rendered
     * @param module the module to render the settings for
     */
    public void showOptions(Module module) {
        openedOptions.put(module, true);
    }

    private static HashMap<Class<?>, Boolean> renderableSettingTypes = new HashMap<>() {{
        put(Boolean.class, true);
        put(String.class, true);
        put(Integer.class, true);
        put(Double.class, true);
        put(Mode.class, true);
    }};

    /**
     * Checks if a given setting is supported and can be rendered
     * @param setting the setting to check
     * @return if the setting can be rendered
     */
    private boolean canSettingBeRender(Setting setting) {
        return renderableSettingTypes.containsKey(setting.value.getClass());
    }

    /**
     * Renders a setting for a given module
     * @param mod the module to render the setting for
     * @param setting the setting to render
     * @return if the setting was rendered
     */
    private boolean renderSetting(Module mod, Setting setting) {
        // Make sure that we have the functionality implemented to display the setting
        if (!this.canSettingBeRender(setting)) return false;

        // Make sure that the setting should be rendered
        if (!setting.shouldShow()) return false;

        final String numericalFormat = "%.3f";

        ImGui.pushID(setting.name);

        // Handle different types of settings
        switch (setting.value.getClass().getSimpleName()) {
            // Handle Booleans
            case "Boolean": {
                if (ImGui.checkbox(setting.getNiceName(), (Boolean)setting.value)) {
                    setting.value = !(Boolean)setting.value; // It was toggled
                }

                break;
            }

            // Handle Strings
            case "String": {
                ImString str = new ImString((String)setting.value, 128);
                ImGui.inputText(setting.getNiceName(), str);
                setting.value = str.toString();

                break;
            }

            // Handle Double
            case "Double": {
                // Get the current value
                ImDouble value = new ImDouble((Double)setting.value);

                // Set the width for the bar
                ImGui.pushItemWidth(ImGui.getFontSize() * 6);

                boolean changed = false;
                // Check to see if we have a range
                if (setting.hasRange()) {
                    double min = (double) setting.getMin();
                    double max = (double) setting.getMax();

                    // Render the slider
                    changed = ImGui.sliderScalar(setting.getNiceName(), ImGuiDataType.Double, value, min, max, numericalFormat);
                } else {
                    changed = ImGuiUtils.accurateDoubleInput(setting.getNiceName(), value, numericalFormat);
                }

                // Update the value
                if (changed) {
                    setting.value = value.get();
                }

                // Pop the width
                ImGui.popItemWidth();

                break;
            }

            // Handle integers
            case "Integer": {
                ImInt value = new ImInt((Integer)setting.value);

                boolean changed = false;

                ImGui.pushItemWidth(ImGui.getFontSize() * 6);

                if (setting.hasRange()) {
                    int min = (int) setting.getMin();
                    int max = (int) setting.getMax();

                    changed = ImGui.sliderScalar(setting.getNiceName(), ImGuiDataType.S32, value, min, max);
                } else {
                    changed = ImGui.inputInt(setting.getNiceName(), value, 0, 0);
                }

                if (changed) {
                    setting.value = value.get();
                }

                ImGui.popItemWidth();

                break;
            }

            case "Mode": {
                // Get the current mode
                Mode mode = (Mode) setting.value;

                int currentIndex = 0;

                List<String> modes = new ArrayList<>();
                for (String m : mode.getEntries()) {
                    modes.add(m);
                }

                // Find the current index
                currentIndex = mode.getState();

                // Setup ImInt
                ImInt imInt = new ImInt(currentIndex);

                // Get the string array
                String[] modeStrings = modes.toArray(new String[modes.size()]);

                // Render the combo box
                if (ImGui.combo(setting.getNiceName(), imInt, modeStrings)) {
                    mode.setState(imInt.get());
                }

                break;
            }

            default: {
                ImGui.popID();
                return false;
            }
        }

        // Show setting tool tip
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip(setting.getToolTip());
        }

        ImGui.popID();
        return true;
    }

    /**
     * Toggles the settings from being rendered for a given module
     * @param module the module to toggle the settings for
     * @return if the settings are now being rendered
     */
    public boolean toggleOptions(Module module) {
        // Hide the module is already added then remove it to hide it
        if (this.shouldShowOptions(module)) {
            this.hideOptions(module);
            return false;
        }

        // Otherwise add the module to the opened settings hashmap
        this.showOptions(module);
        return true;
    }



    public static void onRenderVideo(MatrixStack matrixStack, float partialTicks)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        int currentFrame = (int)Math.floor(ticks);
        int nextFrame = (int)Math.ceil(ticks);
        int alpha = ticks - currentFrame;
        Identifier currentFrameTexture = frames[currentFrame];
        Identifier nextFrameTexture = frames[nextFrame];
        RenderSystem.setShaderTexture(0, currentFrameTexture);
        Window sr = MinecraftClient.getInstance().getWindow();
        int x = 0;
        int y = 0;
        int w = sr.getScaledWidth();
        int h = sr.getScaledHeight();
        DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, w, h, w, h);

        if (alpha > 0) {
            RenderSystem.setShaderTexture(0, nextFrameTexture);
            DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, w, h, w, h, 1 - alpha);
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
    }







    /**
     * Renders a darkened background
     * @param matrices the matrix stack
     * @param tickDelta the tick delta
     */
    public void renderBackground(MatrixStack matrices, float tickDelta) {
        // Current background darkness
        float current = this.backgroundDarkness;

        float next = this.getNext(current, BACKGROUND_DARKNESS_SPEED, BACKGROUND_DARKNESS_MIN, 1.0f);

        // Get lerped background darkness
        float backgroundDarkness = this.lerpValue(current, next, tickDelta);

        // Actual background darkness
        float d = 1 - backgroundDarkness;

        // Get the start and end colours
        int topRed = getClickGUI().getIntSetting("GradientTopRed");
        int topGreen = getClickGUI().getIntSetting("GradientTopGreen");
        int topBlue = getClickGUI().getIntSetting("GradientTopBlue");
        int topOpacity = getClickGUI().getIntSetting("TopOpacity");

        int bottomRed = getClickGUI().getIntSetting("GradientBottomRed");
        int bottomGreen = getClickGUI().getIntSetting("GradientBottomGreen");
        int bottomBlue = getClickGUI().getIntSetting("GradientBottomBlue");
        int bottomOpacity = getClickGUI().getIntSetting("BottomOpacity");

        //Colour s = new Colour(54, 17, 79, 192 * d);
        //Colour e = new Colour(110, 53, 176, 192 * d);
        Colour s = new Colour(topRed, topGreen, topBlue, topOpacity * d);
        Colour e = new Colour(bottomRed, bottomGreen, bottomBlue, bottomOpacity * d);

        this.fillGradient(matrices, 0, 0, this.width, this.height, s.toARGB(), e.toARGB());
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private float backgroundDarkness = 1.0f;
    private final static float BACKGROUND_DARKNESS_SPEED = -0.10f;
    private final static float BACKGROUND_DARKNESS_MIN = 0f;

    /**
     * Gets the scale for the GUI
     * @return
     */
    private float getScale() {
        double scale = this.getClickGUI().getDoubleSetting("Scale");

        return (float) scale;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getClickGUI().isEnabled()) this.close();

        // Handle reset next
        if (resetNext) {
            resetNext = false;
            
            // Clear the search
            searchPhrase = "";

            // Clear the opened settings
            openedOptions.clear();
        }

        // Update the global ImGUI scale
        ImGui.getIO().setFontGlobalScale(this.getScale());

        // Background darkness
        if (this.backgroundDarkness > BACKGROUND_DARKNESS_MIN) {
            this.backgroundDarkness += BACKGROUND_DARKNESS_SPEED;
        }

        // Category tones
        if (this.emptyBgTone > EMPTY_BG_TONE_MIN) {
            this.emptyBgTone += EMPTY_BG_TONE_SPEED;
        }

        // Sync the felixes
        this.syncBouncySettings();

        // Bouncy ticks
        for (BouncyWidget widget : this.bouncyWidgets) {
            widget.tick();
        }
    }

    // Search category tones
    private float emptyBgTone = 1f;
    private final static float EMPTY_BG_TONE_SPEED = -0.05f;
    private final static float EMPTY_BG_TONE_MIN = 0.5f;

    /**
     * Gets the next value for a given value, speed and min
     * @param curr the current value
     * @param speed the speed to change the value by
     * @param min the minimum value
     * @param max the maximum value
     * @return a clamped next value
     */
    private float getNext(float curr, float speed, float min, float max) {
        return Math.max(min, Math.min(max, curr + speed));
    }

    private float lerpValue(float current, float next, float tickDelta) {
        return current + (next - current) * tickDelta;
    }

    private static int getSaveCondition() {
        return !resetNext ? ImGuiCond.FirstUseEver : ImGuiCond.Always; 
    }

    /**
     * Render the bind button
     * @param mod the module to render the bind button for
     */
    private void renderBindButton(Module mod) {
        ImGui.pushID(mod.getName()+"_bind");

        // Get the default button text
        String boundText = this.getBoundKey(mod) == -1 ? "Add bind" : "Key " + ClientUtils.getKeyCodeName(this.getBoundKey(mod)).toUpperCase();

        // Further process the button text (if the key is waiting to be bound)
        boundText = this.bindNext == mod ? "Waiting for a key..." : boundText;

        // Render the bind button
        if (ImGui.button(boundText)) {
            this.bindNext = this.bindNext == mod ? null : mod;
        }

        if (ImGui.isItemHovered()) {
            // Add tooltip to the button
            ImGui.setTooltip("Press ESCAPE to cancel, BACKSPACE to remove, or any other key to bind.");
        }

        ImGui.popID();
    }

    /**
     * Renders edge cases for different modules, for example the ClickGUI's reset button
     * @param mod the module to render the edge case for
     */
    private void renderOptionEdgeCase(Module mod) {
        // Handle the stupid reset button case
        if (mod instanceof ClickGUI) {
            // Render the reset button

            // Center the button text
            ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f);

            // Render the reset button on the same line as the "bind button"
            ImGui.sameLine();

            if (ImGui.button("Reset All", 0, 0)) {
                resetNext = true;
            }

            ImGui.popStyleVar(1);
        }
    }

    /**
     * Renders a given setting category
     * @param mod the module to render the settings for
     * @param settings the settings to render
     * @return if the settings were rendered
     */
    private boolean renderSettingsCategory(Module mod, Setting[] settings) {
        if (settings.length == 0) return false;

        // Get the first setting
        Setting firstSetting = settings[0];

        // Get the category name
        String categoryName = firstSetting.hasCategory() ? firstSetting.getCategory() : "Uncategorised";

        // Render an ImGui category
        ImGui.pushID(categoryName);

        boolean doDropDown = firstSetting.hasCategory();
        boolean shouldRender = !doDropDown;

        if (doDropDown) {
            shouldRender = ImGui.collapsingHeader(categoryName);
        }

        // Render the settings
        if (shouldRender) {
            if (doDropDown) {
                // Render indentation and separator
                ImGui.indent();
            }

            for (Setting setting : settings) {
                if (!setting.shouldShow()) continue;

                // Render the setting
                this.renderSetting(mod, setting);
            }

            // Unindent
            if (doDropDown) {
                // Unindent
                ImGui.spacing();
                ImGui.unindent();
            }
        }

        // Pop the ID
        ImGui.popID();

        return shouldRender;
    }

    /**
     * Renders the module settings
     * @param mod the module to render the settings for
     * @return if the module settings were rendered
     */
    private boolean renderSettings(Module mod) {
        if (mod.getSettings().isEmpty()) return false;

        HashMap<String, List<Setting>> settingCategory = new HashMap<>();
        List<Setting> uncategorised = new ArrayList<>();

        // Get the settings categories
        for (String settingName : mod.getSettings()) {
            // Get the setting
            Setting setting = mod.getSetting(settingName);

            // Check if we even should show this setting
            if (!setting.shouldShow()) continue;

            // Check that the setting has a category
            if (!setting.hasCategory()) {
                // Add to the uncategorised list
                uncategorised.add(setting);
                
                continue;
            }

            // Get the category
            String category = setting.getCategory();

            // Check if the category exists
            if (!settingCategory.containsKey(category)) {
                // Create the category
                settingCategory.put(category, new ArrayList<>());
            }

            // Add the setting to the category
            settingCategory.get(category).add(setting);
        }

        // Render the settings
        for (String category : settingCategory.keySet()) {
            Setting settings[] = settingCategory.get(category).toArray(Setting[]::new);

            this.renderSettingsCategory(mod, settings);
        }

        // Render the uncategorised settings
        if (!uncategorised.isEmpty()) {
            this.renderSettingsCategory(mod, uncategorised.toArray(Setting[]::new));
        }

        return true;
    }

    /**
     * Render the module's options
     * @param mod the module to render the options for
     */
    private void renderModuleOptions(Module mod) {
        ImGui.indent();
        ImGui.separator();

        // Render the bind button
        this.renderBindButton(mod);

        // Render the edge cases
        this.renderOptionEdgeCase(mod);

        // Render the settings
        this.renderSettings(mod);

        ImGui.separator();
        ImGui.unindent();
    }

    /**
     * Render a given module
     * @param mod the module to render
     */
    private void renderModule(Module mod) {
        // Push a new id
        ImGui.pushID(mod.getName());

        // Push style variable
        ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.0f, 0.5f);


        //NIGGERS

        // Push enabled style
        //MODULE BUTTON
        if (mod.isEnabled()) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0.44f, 0.00f, 0.94f, 1f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.44f, 0.00f, 0.94f, 1f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.44f, 0.00f, 0.94f, 1f);

        }


        boolean shouldToggle = ImGui.button(mod.getName(), ImGui.getWindowWidth() - ImGui.getStyle().getWindowPaddingX()*2, 0);

        // Pop enabled style
        if (mod.isEnabled()) {
            ImGui.popStyleColor(3);
        }

        // Handle if the mouse is being hovered
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip(mod.getDescription() == null ? "No description, sorry :(" : mod.getDescription());

            // Handle right clicks
            if (ImGui.isMouseClicked(1)) {
                this.toggleOptions(mod);
            }
        }

        // Render the module options
        if (this.shouldShowOptions(mod)) this.renderModuleOptions(mod);

        // Pop the style variable
        ImGui.popStyleVar(1);

        // Handle outputs

        // Draw button that toggles the module
        if (shouldToggle) {
            ChatUtils.hideNextChat = true;
            mod.toggle();
        }

        // Pop the id
        ImGui.popID();
    }

    /**
     * Creates windows for all the categories and populates them with modules
     */
    private void renderModules(float tickDelta) {        
        // Default padding
        final float xPadding = 15 * this.getScale();
        final float yPadding = 15 * this.getScale();
        final float yOffset  = 80 * this.getScale();

        // For default positioning
        float nextXPos = xPadding;

        // For default width/height
        final float defaultWidth = 175f * this.getScale();

        // Store previous heights
        List<Float> prevHeights = new ArrayList<>();
        int heightPtr = 0;

        float emptyBgTone = this.emptyBgTone;

        // Get next empty background tone
        float next = this.getNext(emptyBgTone, EMPTY_BG_TONE_SPEED, EMPTY_BG_TONE_MIN, 1.0f);

        // Lerp empty background tone
        emptyBgTone = this.lerpValue(this.emptyBgTone, next, tickDelta);

        for (String cat : categories.keySet()) {
            // Get the list of modules for the category as a copy
            List<Module> modules = new ArrayList<>(categories.get(cat));

            // Remove all of the modules from the list that don't contain the search phrase
            if (searchPhrase != null && !searchPhrase.isEmpty() && !resetNext) {
                modules.removeIf(mod -> !mod.getName().toLowerCase().contains(searchPhrase.toLowerCase()));
            }

            // Sort the list alphabetically
            modules.sort((a, b) -> a.getName().compareTo(b.getName()));

            // Check the category is not empty
            if (modules.isEmpty()) {
                // Change the background darkness to make it obvious that the category is empty
                ImGui.setNextWindowBgAlpha(emptyBgTone);
            }

            // First ever positioning
            float yPos = prevHeights.size() < heightPtr + 1 ? yPadding + yOffset : prevHeights.get(heightPtr) + yPadding;

            float currentHeight = (modules.size() + 1) * 30;

            // Set first ever position
            ImGui.setNextWindowSize(defaultWidth, currentHeight, getSaveCondition());
            ImGui.setNextWindowPos(nextXPos, yPos, getSaveCondition());
            
            // Set the window as expanded
            ImGui.setNextWindowCollapsed(false, getSaveCondition());

            // Calculate next positions
            nextXPos += defaultWidth + xPadding;

            // Add the height if the length is less than current pointer
            if (prevHeights.size() < heightPtr + 1) {
                prevHeights.add(yPos + currentHeight);
            } else {
                prevHeights.set(heightPtr, yPos + currentHeight);
            }

            // Increment height pointer
            heightPtr++;

            // Wrap them in case they're off the screen
            if (nextXPos + defaultWidth + xPadding > WalksyClient.getClient().getWindow().getWidth()) {
                nextXPos = xPadding;
                heightPtr = 0;
            }

            // Show collapse button
            ImGui.begin(cat);

            for (Module mod : modules) {
                this.renderModule(mod);
            }

            ImGui.end();

            // Reset the mouse count
            ImGui.resetMouseDragDelta();
        }
    }

    /**
     * The current search phrase
    */
    private static String searchPhrase = "";

    public static boolean resetNext = false;

    /**
     * Render the search window
     * @param tickDelta tick delta
     * @return the search phrase
     */
    private String renderSearch(float tickDelta) {
        // Set the default position
        ImGui.setNextWindowPos(this.width - 100f*getScale(), 16* this.getScale(), getSaveCondition());

        // Set the window size
        // Set the scaled window size
        ImGui.setNextWindowSize(200f * this.getScale(), 0);

        // Make it so that the window is just enough to fit a textbox in
        ImGui.setNextWindowContentSize(0, 0f);

        // Begin the window
        ImGui.begin("Search", ImGuiWindowFlags.NoResize);
        // Hover the search button
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip("Search for a module");
        }

        ImGui.pushItemWidth(-1);
        // Render the search input
        ImString str = new ImString(searchPhrase, 64);
        ImGui.inputTextWithHint(Strings.EMPTY, "e.g. " + this.searchExample, str);

        // Check for changes in the search phrase
        if (!str.toString().equals(searchPhrase)) {
            // Update the background tone
            this.emptyBgTone = 1f;
        }

        searchPhrase = str.toString();

        ImGui.popItemWidth();

        ImGui.end();

        return searchPhrase;
    }

    @Override
    protected void renderImGui(float tickDelta) {
        this.renderModules(tickDelta);
        this.renderSearch(tickDelta);
    }

    private ClickGUI getClickGUI() {
        return (ClickGUI) WalksyClient.getInstance().getModules().get("clickgui");
    }

    @Override
    public void close() {
        // Hide the chat output
        ChatUtils.hideNextChat = true;

        // Disable the module
        this.getClickGUI().disable();

        super.close();
    }

    public static int ticks = 0;


    public static void onTick() {

        if(ticks >= 201)
            ticks = 0;
        else
            ticks++;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // Render background
        if (this.getClickGUI().getModeSetting("Mode").is("Gradient")) {
            this.renderBackground(matrices, delta);
        }
        if (this.getClickGUI().getModeSetting("Mode").is("Video")) {
            onRenderVideo(matrices, delta);
        }

        //HERE



        // Render Bouncy widgets
        for (BouncyWidget widget : this.bouncyWidgets) {
            widget.render(matrices, mouseX, mouseY, delta);
        }

        // Render everything else
        super.render(matrices, mouseX, mouseY, delta);
    }

    // Binds

    private Binds getBinds() {
        return (Binds) WalksyClient.getInstance().getModules().get("binds");
    }

    private HashMap<Module, Integer> activeKeyBinds = new HashMap<>();

    private void populateKeyBinds() {
        activeKeyBinds.clear();

        Binds bindsModule = this.getBinds();
        BindsSystem binds = bindsModule.getBinds();

        for (int key : binds) {
            Queue<Bind> bindQueue = binds.getKeyBinds(key);

            for (Bind bind : bindQueue) {
                if (!(bind instanceof ModuleBind)) continue;

                // Get the module bind
                ModuleBind moduleBind = (ModuleBind)bind;

                // Add the bind
                activeKeyBinds.put(moduleBind.getModule(), key);
            }
        }
    }

    private int getBoundKey(Module module) {
        return this.activeKeyBinds.getOrDefault(module, -1);
    }

    private void removeModuleBinds(Module module) {
        // Get the binds module
        Binds bindsModule = this.getBinds();

        // Get the bind system
        BindsSystem system = bindsModule.getBinds();

        // Unbind other keys
        system.removeBinds(b -> {
            if (!(b instanceof ModuleBind)) return false;

            // Get the module bind
            ModuleBind moduleBind = (ModuleBind)b;

            // Check if the module is the same
            return moduleBind.getModule() == module;
        });
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Make sure that we're binding something
        if (this.bindNext == null) return super.keyPressed(keyCode, scanCode, modifiers);

        // Get the binds module
        Module mod = this.bindNext;

        // Check if the keycode is escape
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.bindNext = null;
            return true;
        }

        // Check if the keycode is backspace
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            // Remove the bind
            this.removeModuleBinds(mod);

            // Populate the key binds
            this.populateKeyBinds();

            // Make sure we're done binding things
            this.bindNext = null;

            return true;
        }

        // Get the binds module
        Binds bindsModule = this.getBinds();

        // Get the bind system
        BindsSystem system = bindsModule.getBinds();

        // Unbind other keys
        this.removeModuleBinds(mod);

        // Add the key bind
        system.addBind(keyCode, new ModuleBind(this.bindNext));

        // Set the bind next to null
        this.bindNext = null;

        // Update the key binds
        this.populateKeyBinds();

        return true;
    }

    private Module bindNext = null;
}
