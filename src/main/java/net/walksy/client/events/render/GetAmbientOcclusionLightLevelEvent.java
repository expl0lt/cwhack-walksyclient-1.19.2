package net.walksy.client.events.render;

import net.walksy.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GetAmbientOcclusionLightLevelEvent extends Event {
    public BlockView blockView;
    public BlockPos blockPos;
    public CallbackInfoReturnable<Float> cir;

    public GetAmbientOcclusionLightLevelEvent(BlockView blockView, BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
        this.cir = cir;
        this.blockView = blockView;
        this.blockPos = blockPos;
    }
}
