package net.walksy.client.modules.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.config.specials.Mode;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.events.render.GetCapeTextureEvent;
import net.walksy.client.modules.DummyModule;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.BlockUtils;
import net.walksy.client.utils.ClientUtils;
import net.walksy.client.utils.CrystalUtils;
import net.walksy.client.utils.InteractionUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

public class Capes extends Module {


    public Capes() {
        super("Capes");

        this.setDescription("Renders a cape on the player");

        this.setCategory("Render");

        this.addSetting(new Setting("Mode", new Mode("Walksy", "Frwost", "WalksyClient", "KFC")));


    }

    private static boolean isEnabled;

    @Override
    public void activate() {
        this.addListen(GetCapeTextureEvent.class);
        isEnabled = true;
    }

    @Override
    public void deactivate() {
        this.removeListen(GetCapeTextureEvent.class);
        isEnabled = false;
    }

    public Identifier getTexture(PlayerEntity player) {
        if (isEnabled && (player == WalksyClient.getClient().player)) {
            return getIdentifier();
        } else {
            return null;
        }
    }


    public Identifier getIdentifier() {
        Identifier iden = new Identifier("walksy-client", "capes/walksyclient.png");
        if (this.getModeSetting("Mode").is("Frwost"))
        {
            iden = new Identifier("walksy-client", "capes/frwost.png");

        }

        else if (this.getModeSetting("Mode").is("WalksyClient"))

        {
            iden = new Identifier("walksy-client", "capes/walksyclient.png");

        }
        else if (this.getModeSetting("Mode").is("KFC"))

        {
            iden = new Identifier("walksy-client", "capes/kfc.png");
        }
        return iden;
    }

    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "GetCapeTextureEvent": {
                GetCapeTextureEvent e = (GetCapeTextureEvent)(event);
                PlayerEntity player = WalksyClient.getClient().player;

                Identifier id = getTexture(((player)));
                if (id != null) e.cir.setReturnValue(id);
            }
        }
    }
}





