package net.walksy.client.mixin.client;

import com.ibm.icu.impl.coll.CollationRoot;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;
import net.walksy.client.WalksyClient;
import net.walksy.client.interfaces.mixin.IClientWorld;
import net.walksy.client.modules.render.Ambience;
import net.walksy.client.utils.Color;
import net.walksy.client.utils.Dimension;
import net.walksy.client.walksyevent.EventManager;
import net.walksy.client.walksyevent.events.EntityDespawnListener;
import net.walksy.client.walksyevent.events.EntitySpawnListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.walksy.client.events.client.GetEntitiesEvent;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import static net.walksy.client.utils.PlayerUtils.getDimension;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements IClientWorld {
    private int skyRed = Ambience.skyRed;
    private int skyGreen = Ambience.skyGreen;
    private int skyBlue = Ambience.skyBlue;

    private int netherSkyRed = Ambience.netherSkyRed;
    private int netherSkyGreen = Ambience.netherSkyGreen;
    private int netherSkyBlue = Ambience.netherSkyBlue;

    private int endSkyRed = Ambience.endSkyRed;
    private int endSkyGreen = Ambience.endSkyGreen;
    private int endSkyBlue = Ambience.endSkyBlue;



    @Unique


    @Inject(at = @At("HEAD"), method = "getEntities()Ljava/lang/Iterable;", cancellable = true)
    private void onGetEntities(CallbackInfoReturnable<Iterable<Entity>> cir) {
        WalksyClient.getInstance().emitter.triggerEvent(new GetEntitiesEvent(cir));
    }

    @Inject(method = "addEntityPrivate", at = @At("TAIL"))
    private void onAddEntityPrivate(int id, Entity entity, CallbackInfo info) {
        if (entity != null)
            EventManager.fire(new EntitySpawnListener.EntitySpawnEvent(entity));
    }

    @Inject(method = "removeEntity", at = @At("TAIL"))
    private void onFinishRemovingEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo info) {
        Entity entity = WalksyClient.getClient().world.getEntityById(entityId);
        if (entity != null)
            EventManager.fire(new EntityDespawnListener.EntityDespawnEvent(entity));
    }

// || Ambience.shouldNetherSky && getDimension() == Dimension.Nether || Ambience.shouldSky && getDimension() == Dimension.End



    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void onGetSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> info) {
        if (Ambience.shouldSky && getDimension() == Dimension.Overworld) {
            Color skyColor = new Color(skyRed, skyGreen, skyBlue);
            Vec3d newSkyColor = new Vec3d(skyColor.r/255f, skyColor.g/255f, skyColor.b/255f);
            info.setReturnValue(newSkyColor);
            info.cancel();
        }
        if (Ambience.shouldNetherSky && getDimension() == Dimension.Nether) {
            Color netherSkyColor = new Color(netherSkyRed, netherSkyGreen, netherSkyBlue);
            Vec3d newSkyColor = new Vec3d(netherSkyColor.r/255f, netherSkyColor.g/255f, netherSkyColor.b/255f);
            info.setReturnValue(newSkyColor);
            info.cancel();
        }
        if (Ambience.shouldEndSky && getDimension() == Dimension.End) {
            Color endSkyColor = new Color(endSkyRed, endSkyGreen, endSkyBlue);
            Vec3d newSkyColor = new Vec3d(endSkyColor.r/255f, endSkyColor.g/255f, endSkyColor.b/255f);
            info.setReturnValue(newSkyColor);
            info.cancel();
        }
    }

    @Inject(method = "getCloudsColor", at = @At("HEAD"), cancellable = true)
    private void onGetCloudsColor(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if (Ambience.shouldCloud) {
            Color cloudColor = new Color(Ambience.cloudRed, Ambience.cloudGreen, Ambience.cloudBlue);
            Vec3d newCloudColor = new Vec3d(cloudColor.r / 255f, cloudColor.g / 255f, cloudColor.b / 255f);
            cir.setReturnValue(newCloudColor);
            cir.cancel();
        }
    }



    @Shadow private PendingUpdateManager pendingUpdateManager;

    @Override
    public PendingUpdateManager obtainPendingUpdateManager() {
        return this.pendingUpdateManager;
    }
}
