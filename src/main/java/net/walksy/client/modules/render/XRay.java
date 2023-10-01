package net.walksy.client.modules.render;

import java.util.HashMap;

import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.render.BlockEntityRenderEvent;
import net.walksy.client.events.render.GetAmbientOcclusionLightLevelEvent;
import net.walksy.client.events.render.ShouldDrawBlockSideEvent;
import net.walksy.client.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.walksy.client.utils.BlockUtils;

@SuppressWarnings("unchecked")
public class XRay extends Module {
    private Boolean fullbrightWasEnabled;

    public XRay() {
        super("XRay");
        this.setDescription("See blocks through the floor.");

        this.addSetting(new Setting("AutoFullbright", true));
        this.addSetting(new Setting("DesiredBlocks", new HashMap<String, Boolean>()));
        // Non-specific search (more lag expected)
        this.addSetting(new Setting("NonSpecificSearch", false));
        this.addSetting(new Setting("BlockSearch", new HashMap<String, Boolean>()));

        this.setCategory("Render");
    }

    private boolean shouldOverrideFullbright() {
        return (this.getBoolSetting("AutoFullbright") && this.fullbrightWasEnabled != null && !this.fullbrightWasEnabled);
    }
    
    private Boolean isDesiredBlock(String blockId) {
        boolean isDesired = this.getHashMapSetting("DesiredBlocks").containsKey(blockId);

        if (!isDesired && this.getBoolSetting("NonSpecificSearch")) {
            HashMap<String, Boolean> blocksToSearch = this.getHashMapSetting("BlockSearch");
            
            for (String phrase : blocksToSearch.keySet()) {
                if (!blocksToSearch.containsKey(phrase) || !blocksToSearch.get(phrase)) continue;

                if (blockId.contains(phrase)) {
                    isDesired = true;
                    break;
                }
            }
        }

        return isDesired;
    }


    @Override
    public void activate() {
        MinecraftClient client = WalksyClient.getClient();
        client.worldRenderer.reload();

        this.fullbrightWasEnabled = WalksyClient.getInstance().getModules().get("fullbright").isEnabled();

        if (this.shouldOverrideFullbright()) WalksyClient.getInstance().getModules().get("fullbright").enable();

        this.addListen(BlockEntityRenderEvent.class);
        // this.addListen(RenderQuadEvent.class);
        this.addListen(ShouldDrawBlockSideEvent.class);
        this.addListen(GetAmbientOcclusionLightLevelEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(BlockEntityRenderEvent.class);
        // this.removeListen(RenderQuadEvent.class);
        this.removeListen(ShouldDrawBlockSideEvent.class);
        this.removeListen(GetAmbientOcclusionLightLevelEvent.class);

        MinecraftClient client = WalksyClient.getClient();
        client.worldRenderer.reload();

        if (this.shouldOverrideFullbright()) WalksyClient.getInstance().getModules().get("fullbright").disable();
        
        this.fullbrightWasEnabled = false;
    }

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "BlockEntityRenderEvent": {
                BlockEntityRenderEvent e = (BlockEntityRenderEvent)event;

                // Get the block id.
                String blockId = BlockUtils.getName(e.blockEntity.getPos());

                // Check if it is not a block we want to see and not display it if that is the case.
                if (!isDesiredBlock(blockId)) e.ci.cancel();

                break;
            }
            case "ShouldDrawBlockSideEvent": {
                ShouldDrawBlockSideEvent e = (ShouldDrawBlockSideEvent)event;

                // Get the blockId as a String
                String blockId = BlockUtils.getName(e.pos);

                // Say if we want it or not.
                e.cir.setReturnValue(isDesiredBlock(blockId));

                // break;
                break;
            }
            case "GetAmbientOcclusionLightLevelEvent": {
                ((GetAmbientOcclusionLightLevelEvent)(event)).cir.setReturnValue(1f);
                break;
            }
            // This can be used to change block transparency.
            // case "RenderQuadEvent": {
            //     RenderQuadEvent e = (RenderQuadEvent)event;
                
            //     String blockId = BlockUtils.getName(e.pos);

            //     if (this.isDesiredBlock(blockId)) break;

            //     // this.rewriteBuffer(e.vertexConsumer, 0);
            //     e.ci.cancel();
                
            //     break;
            // }
        }
    }

    // Currently the accessor is dying so this function doesn't work.
    // private void rewriteBuffer(VertexConsumer vertexConsumer, int alpha) {
    //     if (!(vertexConsumer instanceof BufferBuilder)) return;

    //     BufferBuilder bufferBuilder = (BufferBuilder)vertexConsumer;

    //     // Create an accessor
    //     BufferBuilderAccessor accessor = (BufferBuilderAccessor)bufferBuilder;

    //     // Get the previous offset
    //     int prevOffset = accessor.getElementOffset();
    //     if (prevOffset <= 0) return;

    //     int k = accessor.getVertexFormat().getVertexSizeInteger();
    //     for (int i = 1; i <= 4; i++) {
    //         accessor.setElementOffset(prevOffset - i * k);
    //         bufferBuilder.putByte(15, (byte)(alpha));
    //     }

    //     accessor.setElementOffset(prevOffset);
    // }
}
