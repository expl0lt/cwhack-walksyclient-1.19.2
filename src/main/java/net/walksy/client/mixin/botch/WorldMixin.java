package net.walksy.client.mixin.botch;

import java.util.List;

import net.walksy.client.interfaces.mixin.IWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

@Mixin(World.class)
public class WorldMixin implements IWorld {

    @Shadow
    @Final
    protected List<BlockEntityTickInvoker> blockEntityTickers;

    @Override
    public List<BlockEntityTickInvoker> getBlockEntityTickers() {
        return blockEntityTickers;
    }
    
}
