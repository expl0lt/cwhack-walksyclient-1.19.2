package net.walksy.client.modules.walksymodules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.walksy.client.Main;
import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.ItemUseListener;
import net.walksy.client.walksyevent.events.PlayerTickListener;
import org.lwjgl.glfw.GLFW;

public class TriggerBot extends Module  {


    public TriggerBot() {
        super("TriggerBot");

        this.setDescription("");

        this.setCategory("Combat");

        this.addSetting(new Setting("Cooldown", 1.0d) {{
            this.setMax(1.0d);
            this.setMin(0.0d);
            this.setDescription("cooldown when swinging sword in ticks");
        }});

        this.addSetting(new Setting("AttackInAir", true) {{
            this.setDescription("Whether or not to attack in mid air");
        }});
        this.addSetting(new Setting("AttackOnJump", true) {{
            this.setDescription("Whether or not to attack when jumping");
        }});


    }


    @Override
    public void activate() {
        this.addListen(ClientTickEvent.class);
    }

    @Override
    public void deactivate() {
        this.removeListen(ClientTickEvent.class);
    }




    @Override
    public void fireEvent(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ClientTickEvent": {
                if (WalksyClient.getClient().player.isUsingItem())
                    return;
                if (!(WalksyClient.getClient().player.getMainHandStack().getItem() instanceof SwordItem))
                    return;
                HitResult hit = WalksyClient.getClient().crosshairTarget;
                if (hit.getType() != HitResult.Type.ENTITY)
                    return;
                if (WalksyClient.getClient().player.getAttackCooldownProgress(0) < this.getDoubleSetting("Cooldown"))
                    return;
                Entity target = ((EntityHitResult) hit).getEntity();
                if (!(target instanceof PlayerEntity))
                    return;
                if (!target.isOnGround() && !this.getBoolSetting("AttackInAir"))
                    return;
                if (WalksyClient.getClient().player.getY() > WalksyClient.getClient().player.prevY && !this.getBoolSetting("AttackOnJump"))
                    return;
                WalksyClient.getClient().interactionManager.attackEntity(WalksyClient.getClient().player, target);
                WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
            }
        }
    }




}

