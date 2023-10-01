package net.walksy.client.events.render;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.walksy.client.events.Event;

public class Render3DEvent extends Event {
    public MatrixStack matrices;
    public float tickDelta;
    public boolean cancel = false;
    public double offsetX, offsetY, offsetZ;

    public Render3DEvent(MatrixStack matrices, float tickDelta, double offsetX, double offsetY, double offsetZ) {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

}
