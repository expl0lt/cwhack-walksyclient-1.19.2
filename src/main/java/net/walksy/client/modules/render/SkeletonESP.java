package net.walksy.client.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.render.OnRenderEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.Color;
import net.walksy.client.utils.Rotations;


public class SkeletonESP extends Module {
    public SkeletonESP() {
        super("SkeletonESP");

        this.addSetting(new Setting("Distance", false));

        this.addSetting(new ColorSetting("Red", 0){{
            this.setMax(255);
            this.setMin(0);
        }});

        this.addSetting(new ColorSetting("Green", 0){{
            this.setMax(255);
            this.setMin(0);
        }});
        this.addSetting(new ColorSetting("Blue", 0){{
            this.setMax(255);
            this.setMin(0);
        }});


        this.setDescription("Renders a skeleton on the player");

        this.setCategory("Render");
    }

    @Override
    public void activate() {
        this.addListen(OnRenderEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(OnRenderEvent.class);
    }



    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            // For entity glow
            case "OnRenderEvent": {
                OnRenderEvent e = ((OnRenderEvent) event);
                MatrixStack matrixStack = e.mStack;
                float g = e.tickDelta;

                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(MinecraftClient.isFancyGraphicsOrBetter());
                RenderSystem.enableCull();

                WalksyClient.getClient().world.getEntities().forEach(entity -> {
                    if (!(entity instanceof PlayerEntity)) return;
                    if (WalksyClient.getClient().options.getPerspective() == Perspective.FIRST_PERSON  && WalksyClient.getClient().player == entity) return;
                    int rotationHoldTicks = 4;

                    Color skeletonColor = (new Color(this.getIntSetting("Red"), this.getIntSetting("Green"), this.getIntSetting("Blue"), 255));
                    if (this.getBoolSetting("Distance")) skeletonColor = getColorFromDistance(entity);
                    PlayerEntity playerEntity = (PlayerEntity) entity;

                    Vec3d footPos = getEntityRenderPosition(playerEntity, g);
                    PlayerEntityRenderer livingEntityRenderer = (PlayerEntityRenderer)(LivingEntityRenderer<?, ?>) WalksyClient.getClient().getEntityRenderDispatcher().getRenderer(playerEntity);
                    PlayerEntityModel<PlayerEntity> playerEntityModel = (PlayerEntityModel)livingEntityRenderer.getModel();

                    float h = MathHelper.lerpAngleDegrees(g, playerEntity.prevBodyYaw, playerEntity.bodyYaw);
                    if (WalksyClient.getClient().player == entity && Rotations.rotationTimer < rotationHoldTicks) h = Rotations.serverYaw;
                    float j = MathHelper.lerpAngleDegrees(g, playerEntity.prevHeadYaw, playerEntity.headYaw);
                    if (WalksyClient.getClient().player == entity && Rotations.rotationTimer < rotationHoldTicks) j = Rotations.serverYaw;

                    float q = playerEntity.limbAngle - playerEntity.limbDistance * (1.0F - g);
                    float p = MathHelper.lerp(g, playerEntity.lastLimbDistance, playerEntity.limbDistance);
                    float o = (float) playerEntity.age + g;
                    float k = j - h;
                    float m = playerEntity.getPitch(g);
                    if (WalksyClient.getClient().player == entity && Rotations.rotationTimer < rotationHoldTicks) m = Rotations.serverPitch;

                    playerEntityModel.animateModel(playerEntity, q, p, g);
                    playerEntityModel.setAngles(playerEntity, q, p, o, k, m);

                    boolean swimming = playerEntity.isInSwimmingPose();
                    boolean sneaking = playerEntity.isSneaking();
                    boolean flying = playerEntity.isFallFlying();

                    ModelPart head = playerEntityModel.head;
                    ModelPart leftArm = playerEntityModel.leftArm;
                    ModelPart rightArm = playerEntityModel.rightArm;
                    ModelPart leftLeg = playerEntityModel.leftLeg;
                    ModelPart rightLeg = playerEntityModel.rightLeg;

                    matrixStack.translate(footPos.x, footPos.y, footPos.z);
                    if (swimming) matrixStack.translate(0, 0.35f, 0);

                    matrixStack.multiply(new Quaternion(new Vec3f(0, -1, 0), h + 180, true));
                    if (swimming || flying) matrixStack.multiply(new Quaternion(new Vec3f(-1, 0, 0), 90 + m, true));
                    if (swimming) matrixStack.translate(0, -0.95f, 0);

                    BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                    bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

                    Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
                    bufferBuilder.vertex(matrix4f, 0, sneaking ? 0.6f : 0.7f, sneaking ? 0.23f : 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    bufferBuilder.vertex(matrix4f, 0, sneaking ? 1.05f : 1.4f, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();//spine

                    bufferBuilder.vertex(matrix4f, -0.37f, sneaking ? 1.05f : 1.35f, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();//shoulders
                    bufferBuilder.vertex(matrix4f, 0.37f, sneaking ? 1.05f : 1.35f, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();

                    bufferBuilder.vertex(matrix4f, -0.15f, sneaking ? 0.6f : 0.7f, sneaking ? 0.23f : 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();//pelvis
                    bufferBuilder.vertex(matrix4f, 0.15f, sneaking ? 0.6f : 0.7f, sneaking ? 0.23f : 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();

                    // Head
                    matrixStack.push();
                    matrixStack.translate(0, sneaking ? 1.05f : 1.4f, 0);
                    rotate(matrixStack, head);
                    matrix4f = matrixStack.peek().getPositionMatrix();
                    bufferBuilder.vertex(matrix4f, 0, 0, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    bufferBuilder.vertex(matrix4f, 0, 0.15f, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    matrixStack.pop();

                    // Right Leg
                    matrixStack.push();
                    matrixStack.translate(0.15f, sneaking ? 0.6f : 0.7f, sneaking ? 0.23f : 0);
                    rotate(matrixStack, rightLeg);
                    matrix4f = matrixStack.peek().getPositionMatrix();
                    bufferBuilder.vertex(matrix4f, 0, 0, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    bufferBuilder.vertex(matrix4f, 0, -0.6f, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    matrixStack.pop();

                    // Left Leg
                    matrixStack.push();
                    matrixStack.translate(-0.15f, sneaking ? 0.6f : 0.7f, sneaking ? 0.23f : 0);
                    rotate(matrixStack, leftLeg);
                    matrix4f = matrixStack.peek().getPositionMatrix();
                    bufferBuilder.vertex(matrix4f, 0, 0, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    bufferBuilder.vertex(matrix4f, 0, -0.6f, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    matrixStack.pop();

                    // Right Arm
                    matrixStack.push();
                    matrixStack.translate(0.37f, sneaking ? 1.05f : 1.35f, 0);
                    rotate(matrixStack, rightArm);
                    matrix4f = matrixStack.peek().getPositionMatrix();
                    bufferBuilder.vertex(matrix4f, 0, 0, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    bufferBuilder.vertex(matrix4f, 0, -0.55f, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    matrixStack.pop();

                    // Left Arm
                    matrixStack.push();
                    matrixStack.translate(-0.37f, sneaking ? 1.05f : 1.35f, 0);
                    rotate(matrixStack, leftArm);
                    matrix4f = matrixStack.peek().getPositionMatrix();
                    bufferBuilder.vertex(matrix4f, 0, 0, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    bufferBuilder.vertex(matrix4f, 0, -0.55f, 0).color(skeletonColor.r, skeletonColor.g, skeletonColor.b, skeletonColor.a).next();
                    matrixStack.pop();

                    bufferBuilder.clear();
                    BufferRenderer.drawWithShader(bufferBuilder.end());

                    if (swimming) matrixStack.translate(0, 0.95f, 0);
                    if (swimming || flying) matrixStack.multiply(new Quaternion(new Vec3f(1, 0, 0), 90 + m, true));
                    if (swimming) matrixStack.translate(0, -0.35f, 0);

                    matrixStack.multiply(new Quaternion(new Vec3f(0, 1, 0), h + 180, true));
                    matrixStack.translate(-footPos.x, -footPos.y, -footPos.z);
                });

                RenderSystem.enableTexture();
                RenderSystem.disableCull();
                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(true);
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
            }
        }
    }

    private class ColorSetting extends Setting {
        public ColorSetting(String name, Object value) {
            super(name, value);

            this.setCategory("Color");
        }

        @Override
        public boolean shouldShow() {
            return isEnabled();
        }
    }

    private void rotate(MatrixStack matrix, ModelPart modelPart) {
        if (modelPart.roll != 0.0F) {
            matrix.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(modelPart.roll));
        }

        if (modelPart.yaw != 0.0F) {
            matrix.multiply(Vec3f.NEGATIVE_Y.getRadialQuaternion(modelPart.yaw));
        }

        if (modelPart.pitch != 0.0F) {
            matrix.multiply(Vec3f.NEGATIVE_X.getRadialQuaternion(modelPart.pitch));
        }
    }

    private Vec3d getEntityRenderPosition(Entity entity, double partial) {
        double x = entity.prevX + ((entity.getX() - entity.prevX) * partial) - WalksyClient.getClient().getEntityRenderDispatcher().camera.getPos().x;
        double y = entity.prevY + ((entity.getY() - entity.prevY) * partial) - WalksyClient.getClient().getEntityRenderDispatcher().camera.getPos().y;
        double z = entity.prevZ + ((entity.getZ() - entity.prevZ) * partial) - WalksyClient.getClient().getEntityRenderDispatcher().camera.getPos().z;
        return new Vec3d(x, y, z);
    }

    private Color getColorFromDistance(Entity entity) {
        double distance = WalksyClient.getClient().gameRenderer.getCamera().getPos().distanceTo(entity.getPos());
        double percent = distance / 60;
        Color color = new Color();
        if (percent < 0 || percent > 1) {
            color.set(0, 255, 0, 255);
            return color;
        }

        int r, g;

        if (percent < 0.5) {
            r = 255;
            g = (int) (255 * percent / 0.5);
        } else {
            g = 255;
            r = 255 - (int) (255 * (percent - 0.5) / 0.5);
        }

        color.set(r, g, 0, 255);
        return color;
    }
}
