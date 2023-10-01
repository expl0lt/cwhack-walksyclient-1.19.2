package net.walksy.client.modules.hud;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.render.InGameHudRenderEvent;
import net.walksy.client.modules.DummyModule;
import net.walksy.client.modules.Module;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.jar.JarFile;

public class SelfDestruct extends DummyModule {


    public SelfDestruct() {
        super("SelfDestruct");
        this.setDescription("SelfDestruct");

        this.setCategory("HUD");
    }



    @Override
    public void activate() {
        WalksyClient.getClient().setScreen(null);
        for (Module module : WalksyClient.getInstance().getModules().values()) {
            if (module.isEnabled()) module.disable();
        }
        try {
            System.gc();
            System.runFinalization();
            System.gc();
            Thread.sleep(100L);
            System.gc();
            System.runFinalization();
            Thread.sleep(200L);
            System.gc();
            System.runFinalization();
            Thread.sleep(300L);
            System.gc();
            System.runFinalization();
        } catch (InterruptedException e) {
            WalksyClient.displayChatMessage("Self Destruct Failed.");
            throw new RuntimeException(e);
        }
        System.gc();
            }


    @Override
    public void deactivate() {
    }
}
