package net.walksy.client.modules.render;

import net.walksy.client.WalksyClient;
import net.walksy.client.config.settings.Setting;
import net.walksy.client.events.Event;
import net.walksy.client.events.client.ClientTickEvent;
import net.walksy.client.events.client.PlayerMoveEvent;
import net.walksy.client.events.packet.SendPacketEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.ClientUtils;
import net.walksy.client.utils.MathsUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class FreeCam extends Module {
    private Vec3d origin = new Vec3d(0, 0, 0);
    private float originPitch = 0, originYaw = 0;

    private void setOrigin() {
        this.origin      = WalksyClient.me().getPos();
        this.originYaw   = WalksyClient.me().getYaw();
        this.originPitch = WalksyClient.me().getPitch();
        
    }

    private void revertOrigin() {
        if (this.getBoolSetting("PosReset")) {
            WalksyClient.me().setPos(origin.x, origin.y, origin.z);
            WalksyClient.me().setYaw(this.originYaw);
            WalksyClient.me().setPitch(this.originPitch);
        }

        WalksyClient.me().noClip = false;
    }

    public FreeCam() {
        super("FreeCam");

        this.addSetting(new Setting("Speed", 1f));
        this.addSetting(new Setting("PosReset", true));

        this.setDescription("Allows you to fly around the world (but client-side)");

        this.setCategory("Render");
    }
    
    @Override
    public void activate() {
        if (!ClientUtils.inGame()) {
            this.disable();
            return;
        }

        this.setOrigin();

        this.addListen(ClientTickEvent.class);
        this.addListen(SendPacketEvent.class);
        this.addListen(PlayerMoveEvent.class);

        this.revertOrigin();
    }

    @Override
    public void deactivate() {
        if (!ClientUtils.inGame()) {
            return;
        }

        this.revertOrigin();

        this.removeListen(ClientTickEvent.class);
        this.removeListen(SendPacketEvent.class);
        this.removeListen(PlayerMoveEvent.class);

        this.revertOrigin();
    }

    @Override
    public void fireEvent(Event event) {
        ClientPlayerEntity me = WalksyClient.me();

        switch (event.getClass().getSimpleName()) {
            case "PlayerMoveEvent": {
                me.noClip = true;
                break;
            }
            case "SendPacketEvent": {
                SendPacketEvent e = (SendPacketEvent)event;
                if (e.packet instanceof PlayerMoveC2SPacket || e.packet instanceof ClientCommandC2SPacket) e.ci.cancel();

                break;
            }
            case "ClientTickEvent": {
                // Vals
                Vec3d v = Vec3d.ZERO;
                Vec3d pos = me.getPos();
                float speed = (float)this.getSetting("Speed").value;

                if (me.isSprinting()) speed *= 2;

                // Client settings
                me.setOnGround(true);
                me.setVelocity(0, 0, 0);

                // Game options
                GameOptions opt = WalksyClient.getClient().options;

                // Controls
                if (opt.forwardKey.isPressed()) v = v.add(WalksyClient.me().getRotationVector());
                if (opt.backKey.isPressed())    v = v.add(WalksyClient.me().getRotationVector().multiply(-1));

                if (opt.rightKey.isPressed())   v = v.add(MathsUtils.getRightVelocity(WalksyClient.me()));
                if (opt.leftKey.isPressed())    v = v.add(MathsUtils.getRightVelocity(WalksyClient.me()).multiply(-1));

                if (opt.jumpKey.isPressed())    v = v.add(0,  1, 0);
                if (opt.sneakKey.isPressed())   v = v.add(0, -1, 0);

                // Calculate the speed.
                v   = v.multiply(speed);
                pos = pos.add(v);

                // Set the velocity
                me.setPos(pos.x, pos.y, pos.z);

                break;
            }
        }
    }
}
