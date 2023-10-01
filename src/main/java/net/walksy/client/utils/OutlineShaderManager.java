package net.walksy.client.utils;

import net.minecraft.client.gl.ShaderEffect;
import net.walksy.client.WalksyClient;
import net.walksy.client.mixin.render.WorldRendererAccessor;

public class OutlineShaderManager {

    public static void loadShader(ShaderEffect shader) {
        if (getCurrentShader() != null) {
            getCurrentShader().close();
        }

        ((WorldRendererAccessor) WalksyClient.getClient().worldRenderer).setEntityOutlineShader(shader);
        ((WorldRendererAccessor) WalksyClient.getClient().worldRenderer).setEntityOutlinesFramebuffer(shader.getSecondaryTarget("final"));
    }

    public static void loadDefaultShader() {
        WalksyClient.getClient().worldRenderer.loadEntityOutlineShader();
    }

    public static ShaderEffect getCurrentShader() {
        return ((WorldRendererAccessor) WalksyClient.getClient().worldRenderer).getEntityOutlineShader();
    }
}
