package net.walksy.client.events.render;

import com.ibm.icu.impl.duration.impl.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.walksy.client.events.Event;
import net.walksy.client.utils.ClientUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Render2DEvent extends Event {

    public int screenWidth, screenHeight;
    public double frameTime;
    public float tickDelta;
    public Render2DEvent(int screenWidth, int screenHeight, float tickDelta) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        frameTime = ClientUtils.frameTime;
        this.tickDelta = tickDelta;
    }
}
