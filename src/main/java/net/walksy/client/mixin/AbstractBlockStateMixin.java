package net.walksy.client.mixin;

import net.walksy.client.WalksyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.walksy.client.events.render.GetAmbientOcclusionLightLevelEvent;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(at = @At("RETURN"),
        method = {
            "getAmbientOcclusionLightLevel(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"
        },
        cancellable = true
    )
    private void onGetAmbientOcclusionLightLevel(BlockView blockView, BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
        WalksyClient.getInstance().emitter.triggerEvent(new GetAmbientOcclusionLightLevelEvent(blockView, blockPos, cir));
    }
}
