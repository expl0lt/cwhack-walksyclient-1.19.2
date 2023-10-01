package net.walksy.client.utils;

import com.ibm.icu.impl.duration.impl.Utils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.walksy.client.WalksyClient;
import net.walksy.client.interfaces.mixin.IMatrix4f;
import net.walksy.client.misc.maths.Vec3;
import net.walksy.client.misc.maths.Vec4;

public class NametagUtils {
    private static final Vec4 vec4 = new Vec4();
    private static final Vec4 mmMat4 = new Vec4();
    private static final Vec4 pmMat4 = new Vec4();
    private static final Vec3 camera = new Vec3();
    private static final Vec3 cameraNegated = new Vec3();
    private static Matrix4f model;
    private static Matrix4f projection;
    private static double windowScale;

    public static double scale;

    public static void onRender(MatrixStack matrices, Matrix4f projection) {
        model = matrices.peek().getPositionMatrix().copy();
        NametagUtils.projection = projection;

        camera.set(WalksyClient.getClient().gameRenderer.getCamera().getPos());
        cameraNegated.set(camera);
        cameraNegated.negate();

        windowScale = WalksyClient.getClient().getWindow().calculateScaleFactor(1, false);
    }

    public static boolean to2D(Vec3 pos, double scale) {
        return to2D(pos, scale, true);
    }

    public static boolean to2D(Vec3 pos, double scale, boolean distanceScaling) {
        NametagUtils.scale = scale;
        if (distanceScaling) {
            NametagUtils.scale *= getScale(pos);
        }

        vec4.set(cameraNegated.x + pos.x, cameraNegated.y + pos.y, cameraNegated.z + pos.z, 1);

        ((IMatrix4f) (Object) model).multiplyMatrix(vec4, mmMat4);
        ((IMatrix4f) (Object) projection).multiplyMatrix(mmMat4, pmMat4);

        if (pmMat4.w <= 0.0f) return false;

        pmMat4.toScreen();
        double x = pmMat4.x * WalksyClient.getClient().getWindow().getFramebufferWidth();
        double y = pmMat4.y * WalksyClient.getClient().getWindow().getFramebufferHeight();

        if (Double.isInfinite(x) || Double.isInfinite(y)) return false;

        pos.set(x / windowScale, WalksyClient.getClient().getWindow().getFramebufferHeight() - y / windowScale, pmMat4.z);
        return true;
    }

    public static void begin(Vec3 pos) {
        MatrixStack matrices = RenderSystem.getModelViewStack();

        matrices.push();
        matrices.translate(pos.x, pos.y, 0);
        matrices.scale((float) scale, (float) scale, 1);
    }

    public static void end() {
        RenderSystem.getModelViewStack().pop();
    }

    private static double getScale(Vec3 pos) {
        double dist = camera.distanceTo(pos);
        return MathUtils.clamp(1 - dist * 0.01, 0.5, Integer.MAX_VALUE);
    }
}
