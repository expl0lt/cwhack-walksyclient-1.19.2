package net.walksy.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.walksy.client.mixin.client.ClientPlayNetworkHandlerAccessor;

import java.util.Iterator;

public class WorldChunkIterator implements Iterator<WorldChunk> {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private final int px, pz;
    private final int r;

    private int x, z;
    private WorldChunk chunk;

    public WorldChunkIterator() {
        px = ChunkSectionPos.getSectionCoord(mc.player.getBlockX());
        pz = ChunkSectionPos.getSectionCoord(mc.player.getBlockZ());
        r = getRenderDistance();

        x = px - r;
        z = pz - r;

        nextChunk();
    }

    private void nextChunk() {
        chunk = null;

        while (true) {
            z++;
            if (z > pz + r) {
                z = pz - r;
                x++;
            }

            if (x > px + r || z > pz + r) break;

            chunk = (WorldChunk) mc.world.getChunk(x, z, ChunkStatus.FULL, false);
            if (chunk != null) break;
        }
    }

    @Override
    public boolean hasNext() {
        return chunk != null;
    }

    @Override
    public WorldChunk next() {
        WorldChunk chunk = this.chunk;

        nextChunk();

        return chunk;
    }

    public static int getRenderDistance() {
        int viewDistance = mc.options.getViewDistance().getValue(); // Get the value of the SimpleOption<Integer> object
        int chunkLoadDistance = ((ClientPlayNetworkHandlerAccessor) mc.getNetworkHandler()).getChunkLoadDistance();
        return Math.max(viewDistance, chunkLoadDistance);
    }
}
