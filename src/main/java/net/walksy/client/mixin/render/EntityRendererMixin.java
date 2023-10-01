package net.walksy.client.mixin.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;
import net.walksy.client.WalksyClient;
import net.walksy.client.events.render.RenderNametagEvent;
import net.walksy.client.modules.render.ShowDamageTick;
import net.walksy.client.utils.WorldUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.walksy.client.events.render.renderLabelIfPresentEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Shadow
    @Final
    EntityRenderDispatcher dispatcher;


    @Inject(at = {@At("HEAD")},
            method = {
                    "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            },
            cancellable = true
    )
    private void onRenderLabelIfPresent(T entity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        WalksyClient.getInstance().emitter.triggerEvent(new renderLabelIfPresentEvent<T>(entity, text, matrixStack, vertexConsumerProvider, i, this.dispatcher, ci));
    }

    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    private int checkDamageTicks(TextRenderer instance, Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light, Entity entity) {
        if (entity.isPlayer() && ShowDamageTick.cannotBeHit) {
            text = Text.of(text.getString() + " (IN TICK)");
            x -= 10;
        } else if (entity.isPlayer() && ShowDamageTick.isInAir) {
            text = Text.of(text.getString() + " (IN AIR)");
            x -= 10;
        }
        if (entity.isPlayer() && ShowDamageTick.isInAir && ShowDamageTick.cannotBeHit) {
            text = Text.of(text.getString() + " (IN AIR) & (IN TICK)");
            x -= 10;
        }
        instance.draw(text, x, (float) y, color, false, matrix, vertexConsumers, false, backgroundColor, light);
        return color;
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    public void renderLabel(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof LivingEntity) {
            RenderNametagEvent event = new RenderNametagEvent((LivingEntity) entity, matrices, vertexConsumers, ci);
            if (event.cancel) {
                return;
            }
            WalksyClient.getInstance().emitter.triggerEvent(event);

        }
    }
}
