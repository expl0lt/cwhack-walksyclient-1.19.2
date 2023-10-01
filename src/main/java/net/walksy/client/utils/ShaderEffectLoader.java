package net.walksy.client.utils;

import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ShaderEffectLoader {

    private static final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
    private static final ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();

    public static ShaderEffect load(Framebuffer framebuffer, String name, InputStream input) throws JsonSyntaxException, IOException {
        Identifier id = new Identifier("hypnotic", name);
        return new ShaderEffect(textureManager, (ResourceManager) new OwResourceManager(resourceManager, id, new InputStreamResource(input)), framebuffer, id);
    }

    public static ShaderEffect load(Framebuffer framebuffer, String name, String input) throws JsonSyntaxException, IOException {
        return load(framebuffer, name, new FastByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    private static class InputStreamResource  {

        private InputStream input;

        public InputStreamResource(InputStream input) {
            this.input = input;
        }

    }
    private static class OwResourceManager {

        private ResourceManager resourceMang;
        private Identifier id;
        private InputStreamResource resource;

        public OwResourceManager(ResourceManager resourceMang, Identifier id, InputStreamResource resource) {
            this.resourceMang = resourceMang;
            this.id = id;
            this.resource = resource;
        }
    }
}


