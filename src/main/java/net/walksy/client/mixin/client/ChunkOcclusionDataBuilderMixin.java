package net.walksy.client.mixin.client;

import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.BlockPos;
import net.walksy.client.WalksyClient;
import net.walksy.client.events.client.DisconnectEvent;
import net.walksy.client.events.client.OpaqueCubeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkOcclusionDataBuilder.class)
public class ChunkOcclusionDataBuilderMixin
{
    @Inject(at = {@At("HEAD")},
            method = {"markClosed(Lnet/minecraft/util/math/BlockPos;)V"},
            cancellable = true)
    private void onMarkClosed(BlockPos pos, CallbackInfo ci)
    {
        WalksyClient.getInstance().emitter.triggerEvent(new OpaqueCubeEvent(ci));
    }
}
