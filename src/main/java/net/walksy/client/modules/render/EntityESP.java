package net.walksy.client.modules.render;

import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.config.specials.Mode;
import net.walksy.client.events.Event;
import net.walksy.client.events.render.IsEntityGlowingEvent;
import net.walksy.client.events.render.OnRenderEvent;
import net.walksy.client.events.render.RenderEntityEvent;
import net.walksy.client.misc.Colour;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.RenderUtils;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;


public class EntityESP extends Module {
    public EntityESP() {
        super("EntityESP");

        // Bounding Boxes
        this.addSetting(new Setting("BoxPadding", 0f));
        this.addSetting(new Setting("BlendBoxes", false));

        // Glow
        this.addSetting(new Setting("GlowColour", false));

        // Drawing Mode setting
        this.addSetting(new Setting("Mode", new Mode("Glow", "Box")));

        this.setDescription("Know where entities are more easily.");

        this.setCategory("Render");
    }

    @Override
    public void activate() {
        this.addListen(OnRenderEvent.class);
        this.addListen(IsEntityGlowingEvent.class);
        this.addListen(RenderEntityEvent.class);
    }

    @Override
	public void deactivate() {
        this.removeListen(OnRenderEvent.class);
        this.removeListen(IsEntityGlowingEvent.class);
        this.removeListen(RenderEntityEvent.class);
	}

    private boolean shouldRender(Entity entity) {
        return !(entity instanceof PlayerEntity && (PlayerEntity)entity == WalksyClient.me());
    }

    // Just a wrapper for rendering the boxes
    private void renderBoxes(Entity entity, float tickDelta, MatrixStack mStack) {
        RenderUtils.renderBox(entity, tickDelta, mStack, (Boolean)this.getSetting("BlendBoxes").value, (Float)this.getSetting("BoxPadding").value);
    }

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            // For entity glow
            case "IsEntityGlowingEvent": {
                if (!this.getModeSetting("Mode").is("Glow")) break;

                IsEntityGlowingEvent e = (IsEntityGlowingEvent)event;
                if (!this.shouldRender(e.entity)) break;

                e.cir.setReturnValue(true);

                break;
            }
            case "RenderEntityEvent": {
                if (!this.getModeSetting("Mode").is("Glow")) break;

                RenderEntityEvent e = (RenderEntityEvent)event;

                // Don't bother if we don't want them.
                if (!this.shouldRender(e.entity)) break;

                // Make sure we have the right vertexConsumers
                if (!(e.vertexConsumers instanceof OutlineVertexConsumerProvider)) {
                    break;
                }

                OutlineVertexConsumerProvider outlineVertexConsumers = (OutlineVertexConsumerProvider)(e.vertexConsumers);

                // Calculate what colour we want
                Colour colour = ((Boolean)this.getSetting("GlowColour").value) ? Colour.fromDistance(e.entity) : new Colour(255, 255, 255, 255);

                // Set the colour
                outlineVertexConsumers.setColor((int)colour.r, (int)colour.g, (int)colour.b, (int)colour.a);

                break;
            }
            case "OnRenderEvent": {
                if (!this.getModeSetting("Mode").is("Box")) break;

                OnRenderEvent e = (OnRenderEvent)event;
                Iterable<Entity> ents = WalksyClient.getClient().world.getEntities();

                for (Entity entity : ents) {
                    // Don't render stuff we don't want to see!
                    if (!this.shouldRender(entity)) {
                        continue;
                    }

                    this.renderBoxes(entity, e.tickDelta, e.mStack);
                }
                break;
            }
        }
    }
}
