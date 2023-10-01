package net.walksy.client.components;

import com.mojang.blaze3d.systems.RenderSystem;

import net.walksy.client.WalksyClient;
import net.walksy.client.interfaces.mixin.IMatrix4f;
import net.walksy.client.misc.maths.Vec3;
import net.walksy.client.misc.maths.Vec4;
import net.walksy.client.utils.MathsUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class ProjectionUtils {
    private final Vec4 vec4 = new Vec4();
    private final Vec4 mmMat4 = new Vec4();
    private final Vec4 pmMat4 = new Vec4();
    private final Vec3 camera = new Vec3();

    private final Vec3 cameraNegated = new Vec3();

    private Matrix4f model;
    private Matrix4f projection;

    private double windowScale;

    private static ProjectionUtils instance = null;

    public static ProjectionUtils getInstance() {
        if (instance == null) {
            instance = new ProjectionUtils();
        }

        return instance;
    }

    public void update(MatrixStack matrices, Matrix4f projection) {
        MinecraftClient mc = WalksyClient.getClient();

        model = matrices.peek().getPositionMatrix().copy();
        this.projection = projection;

        camera.set(mc.gameRenderer.getCamera().getPos());
        cameraNegated.set(camera);
        cameraNegated.negate();

        windowScale = mc.getWindow().calculateScaleFactor(1, mc.forcesUnicodeFont());
    }

    public boolean to2D(Vec3 pos, double scale) {
        MinecraftClient mc = WalksyClient.getClient();

        vec4.set(cameraNegated.x + pos.x, cameraNegated.y + pos.y, cameraNegated.z + pos.z, 1);

        ((IMatrix4f) (Object) model).multiplyMatrix(vec4, mmMat4);
        ((IMatrix4f) (Object) projection).multiplyMatrix(mmMat4, pmMat4);

        if (pmMat4.w <= 0.0f) return false;

        pmMat4.toScreen();
        double x = pmMat4.x * mc.getWindow().getFramebufferWidth();
        double y = pmMat4.y * mc.getWindow().getFramebufferHeight();

        if (Double.isInfinite(x) || Double.isInfinite(y)) return false;

        pos.set(x / windowScale / scale, (mc.getWindow().getFramebufferHeight() - y / windowScale) / scale, pmMat4.z);
        return true;
    }

    public static double getScale(Vec3d pos, float tickDelta) {
        double dist = WalksyClient.getClient().cameraEntity.getLerpedPos(tickDelta).distanceTo(pos);

        return MathsUtils.clamp(
            1 - dist / 100,
            0.5,
            Double.MAX_VALUE
        );
    }

    public static double getScale(Vec3 pos, float tickDelta) {
        return getScale(pos.to3d(), tickDelta);
    }

    public static void unscaledProjection() {
        MinecraftClient mc = WalksyClient.getClient();

        RenderSystem.setProjectionMatrix(Matrix4f.projectionMatrix(0, mc.getWindow().getFramebufferWidth(), 0, mc.getWindow().getFramebufferHeight(), 1000, 3000));
    }

    public static void scaleProjection(float scale) {
        MinecraftClient mc = WalksyClient.getClient();

        RenderSystem.setProjectionMatrix(Matrix4f.projectionMatrix(0, mc.getWindow().getFramebufferWidth() / scale, 0, mc.getWindow().getFramebufferHeight() / scale, 1000, 3000));
    }

    public static void resetProjection() {
        MinecraftClient mc = WalksyClient.getClient();
        
        RenderSystem.setProjectionMatrix(Matrix4f.projectionMatrix(0, (float) (mc.getWindow().getFramebufferWidth() / mc.getWindow().getScaleFactor()), 0, (float) (mc.getWindow().getFramebufferHeight() / mc.getWindow().getScaleFactor()), 1000, 3000));
    }
}
