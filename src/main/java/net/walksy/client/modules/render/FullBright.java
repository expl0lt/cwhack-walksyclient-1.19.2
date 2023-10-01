package net.walksy.client.modules.render;

import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.config.specials.Mode;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.interfaces.mixin.SimpleOptionAccessor;
import net.walksy.client.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright extends Module {
    private Double normalGamma = 0d;
    Boolean hasSetGamma = false;

    public FullBright() {
        super("FullBright");
        
        this.setDescription("Allows you to see anywhere as if it was day.");

        this.addSetting(new Setting("Mode", new Mode("Gamma", "Potion")));

        this.setCategory("Render");
    }

    @Override
    public String listOption() {
        return this.getModeSetting("Mode").getStateName();
    }
    
    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
    }

    private void restoreGamma() {
        if (!this.hasSetGamma) return;

        MinecraftClient client = WalksyClient.getClient();
        client.options.getGamma().setValue(this.normalGamma);
        this.hasSetGamma = false;
    }

    private void restoreEffect() {
        if (WalksyClient.me().hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            WalksyClient.me().removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);

        this.restoreGamma();
        this.restoreEffect();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                switch (this.getModeSetting("Mode").getStateName()) {
                    case "Potion": {
                        // Restore other mod's gamma
                        this.restoreGamma();

                        WalksyClient.me().addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 3, 1, true, true));

                        break;
                    }

                    case "Gamma": {
                        MinecraftClient client = WalksyClient.getClient();

                        if (!this.hasSetGamma) {
                            this.normalGamma = client.options.getGamma().getValue();
                            this.hasSetGamma = true;
        
                            this.restoreEffect();
                        }
        
                        SimpleOptionAccessor<Double> accessor = (SimpleOptionAccessor<Double>)(Object)(client.options.getGamma());
                        accessor.setUnsafeValue(16d);

                        break;
                    }
                }
            }
        }
    }
}
