package net.walksy.client.modules.render;

import java.util.HashMap;
import java.util.List;

import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.render.OnRenderEvent;
import net.walksy.client.events.render.RenderWorldViewBobbingEvent;
import net.walksy.client.interfaces.mixin.IEntity;
import net.walksy.client.interfaces.mixin.IWorld;
import net.walksy.client.misc.Colour;
import net.walksy.client.misc.EntityFlags;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.BlockUtils;
import net.walksy.client.utils.MathsUtils;
import net.walksy.client.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

public class Tracers extends Module {

    public Tracers() {
        super("Tracers");

        this.addSetting(new Setting("Player", true));
        this.addSetting(new Setting("Mob", true));
        this.addSetting(new Setting("Item", true));
        this.addSetting(new Setting("OtherEntities", true));
        this.addSetting(new Setting("Invisible", false));
        
        // TODO add block search
        // TODO add entity search mode

        // Block/Ticker search
        this.addSetting(new Setting("Block", false));
        this.addSetting(new Setting("Blocks", new HashMap<String, Boolean>()));

        // Rendering
        this.addSetting(new Setting("Transparency", 1f));
    
        this.setDescription("Draws tracers to specified targets.");
        this.setCategory("Render");
    }
    
    @Override
    public void activate() {
        this.addListen(OnRenderEvent.class);
        this.addListen(RenderWorldViewBobbingEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(OnRenderEvent.class);
        this.removeListen(RenderWorldViewBobbingEvent.class);
    }

    public boolean shouldDrawTracer(Entity ent) {
        boolean drawPlayer        = (boolean)this.getSetting("Player").value;
        boolean drawMob           = (boolean)this.getSetting("Mob").value;
        boolean drawItem          = (boolean)this.getSetting("Item").value;
        boolean drawOtherEntities = (boolean)this.getSetting("OtherEntities").value;
        boolean drawInvisible     = (boolean)this.getSetting("Invisible").value;

        IEntity iEnt = (IEntity)ent;

        boolean isMob       = ent instanceof MobEntity;
        boolean isPlayer    = ent instanceof PlayerEntity;
        boolean isItem      = ent instanceof ItemEntity;
        boolean isInvisible = iEnt.getEntFlag(EntityFlags.INVISIBLE_FLAG_INDEX);

        return (
                (drawPlayer && isPlayer)
            ||  (drawMob && isMob)
            ||  (drawItem && isItem)
            ||  (drawInvisible && isInvisible)
            ||  (drawOtherEntities && !isMob && !isPlayer && !isItem)
        );
    }

    @SuppressWarnings("unchecked")
    public boolean shouldDrawTracer(BlockEntityTickInvoker block) {
        HashMap<String, Boolean> phrases = (HashMap<String, Boolean>)this.getSetting("Blocks").value;

        return phrases.containsKey(block.getName());
    }

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "RenderWorldViewBobbingEvent": {
                RenderWorldViewBobbingEvent e = (RenderWorldViewBobbingEvent)event;
                e.cancel = true;
                

                break;
            }

            case "OnRenderEvent": {
                OnRenderEvent e = (OnRenderEvent)event;
                float transparency = (Float)this.getSetting("Transparency").value;

                // Render entity/player tracers
                Iterable<Entity> ents = WalksyClient.getClient().world.getEntities();
                for (Entity entity : ents) {
                    if (!this.shouldDrawTracer(entity)) continue;

                    // No render myself.
                    if (entity instanceof PlayerEntity && (PlayerEntity)entity == WalksyClient.me()) {
                        continue;
                    }

                    // TODO add different colours for different entities
                    Colour c = WalksyClient.getInstance().config.entityColour;

                    // Render tracers
                    RenderUtils.drawTracer(e.mStack, MathsUtils.getLerpedCentre(entity, e.tickDelta), e.tickDelta, c.r, c.g, c.b, c.a*transparency);
                }

                if ((boolean)this.getSetting("Block").value) {
                    // Render blocks etc.
                    List<BlockEntityTickInvoker> tickers = ((IWorld)(WalksyClient.getClient().world)).getBlockEntityTickers();
                    for (BlockEntityTickInvoker ticker : tickers) {
                        if (!this.shouldDrawTracer(ticker)) continue;

                        // This is only storage colouring for now but whatever.
                        // TODO what if it isn't storage!?
                        Colour c = WalksyClient.getInstance().config.storageColour;

                        RenderUtils.drawTracer(e.mStack, BlockUtils.blockPos(ticker.getPos()).add(0.5, 0.5, 0.5), e.tickDelta, c.r, c.g, c.b, c.a * transparency);
                    }
                }

                break;
            }
        }
    }
}
