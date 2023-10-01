package net.walksy.client.modules.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.walksy.client.Main;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

public class Ambience extends Module  {


    public Ambience() {
        super("Ambience");

        this.setDescription("Change color of curtain things");

        this.setCategory("Render");

        this.addSetting(new Setting("Sky", false) {{
            this.setDescription("Change sky color");
        }});
        this.addSetting(new Setting("NetherSky", false) {{
            this.setDescription("Change sky color");
        }});
        this.addSetting(new Setting("EndSky", false) {{
            this.setDescription("Change sky color");
        }});
        this.addSetting(new Setting("CustomCloud", false) {{
            this.setDescription("Change cloud color");
        }});




        this.addSetting(new SkySetting("SkyRed", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of red in the sky");
        }});

        this.addSetting(new SkySetting("SkyGreen", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of green in the sky");
        }});

        this.addSetting(new SkySetting("SkyBlue", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of blue in the sky");
        }});

//nether

        this.addSetting(new NetherSky("NetherSkyRed", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of red in the sky");
        }});

        this.addSetting(new NetherSky("NetherSkyGreen", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of green in the sky");
        }});

        this.addSetting(new NetherSky("NetherSkyBlue", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of blue in the sky");
        }});

//end

        this.addSetting(new EndSky("EndSkyRed", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of red in the sky");
        }});

        this.addSetting(new EndSky("EndSkyGreen", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of green in the sky");
        }});

        this.addSetting(new EndSky("EndSkyBlue", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of blue in the sky");
        }});

//cloud

        this.addSetting(new CustomCloud("CloudRed", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of red in the cloud");
        }});

        this.addSetting(new CustomCloud("CloudGreen", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of green in the cloud");
        }});

        this.addSetting(new CustomCloud("CloudBlue", 0) {{
            this.setMax(255);
            this.setMin(0);
            this.setDescription("The amount of blue in the cloud");
        }});

    }


    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
        reload();
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
        reload();
    }

public static boolean shouldSky;
    public static int skyRed;
    public static int skyGreen;
    public static int skyBlue;

    public static boolean shouldNetherSky;
    public static int netherSkyRed;
    public static int netherSkyGreen;
    public static int netherSkyBlue;


    public static boolean shouldEndSky;
    public static int endSkyRed;
    public static int endSkyGreen;
    public static int endSkyBlue;

    public static boolean shouldCloud;
    public static int cloudRed;
    public static int cloudGreen;
    public static int cloudBlue;



    private void reload()
    {
        if (WalksyClient.getClient().worldRenderer != null && isEnabled()) WalksyClient.getClient().worldRenderer.reload();
    }

    public static class Custom extends DimensionEffects {
        public Custom() {
            super(Float.NaN, true, DimensionEffects.SkyType.END, true, false);
        }

        @Override
        public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
            return color.multiply(0.15000000596046448D);
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return false;
        }

        @Override
        public float[] getFogColorOverride(float skyAngle, float tickDelta) {
            return null;
        }
    }

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                shouldSky = this.getBoolSetting("Sky");
                shouldNetherSky = this.getBoolSetting("NetherSky");
                shouldEndSky = this.getBoolSetting("EndSky");
                shouldCloud = this.getBoolSetting("CustomCloud");
            }
            if (shouldSky) {
                skyRed = this.getIntSetting("SkyRed");
                skyGreen = this.getIntSetting("SkyGreen");
                skyBlue = this.getIntSetting("SkyBlue");
            }
            if (shouldNetherSky) {
                netherSkyRed = this.getIntSetting("NetherSkyRed");
                netherSkyGreen = this.getIntSetting("NetherSkyGreen");
                netherSkyBlue = this.getIntSetting("NetherSkyBlue");
            }
            if (shouldEndSky) {
                endSkyRed = this.getIntSetting("EndSkyRed");
                endSkyGreen = this.getIntSetting("EndSkyGreen");
                endSkyBlue = this.getIntSetting("EndSkyBlue");
            }
            if (shouldCloud) {
                cloudRed = this.getIntSetting("CloudRed");
                cloudGreen = this.getIntSetting("CloudGreen");
                cloudBlue = this.getIntSetting("CloudBlue");
            }
        }
    }



    private class SkySetting extends Setting {
        public SkySetting(String name, Object value) {
            super(name, value);

            this.setCategory("Sky");
        }

        @Override
        public boolean shouldShow() {
            return getBoolSetting("Sky");
        }
    }

    private class NetherSky extends Setting {
        public NetherSky(String name, Object value) {
            super(name, value);

            this.setCategory("NetherSky");
        }

        @Override
        public boolean shouldShow() {
            return getBoolSetting("NetherSky");
        }
    }


    private class EndSky extends Setting {
        public EndSky(String name, Object value) {
            super(name, value);

            this.setCategory("EndSky");
        }

        @Override
        public boolean shouldShow() {
            return getBoolSetting("EndSky");
        }
    }


    private class CustomCloud extends Setting {
        public CustomCloud(String name, Object value) {
            super(name, value);

            this.setCategory("CloudColor");
        }

        @Override
        public boolean shouldShow() {
            return getBoolSetting("CustomCloud");
        }
    }
}


