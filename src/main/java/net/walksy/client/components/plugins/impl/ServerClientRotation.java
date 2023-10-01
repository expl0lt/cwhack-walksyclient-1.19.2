package net.walksy.client.components.plugins.impl;

import net.walksy.client.components.plugins.ModulePlugin;
import net.walksy.client.events.Event;
import net.walksy.client.events.packet.PostMovementPacketEvent;
import net.walksy.client.events.packet.PreMovementPacketEvent;
import net.walksy.client.modules.Module;
import net.walksy.client.utils.ClientUtils;
import net.walksy.client.utils.RotationUtils;
import net.walksy.client.utils.RotationUtils.Rotation;
import net.minecraft.util.math.Vec3d;

public class ServerClientRotation implements ModulePlugin {
    public void handlePreMotion() {
        this.client = ClientUtils.getRotation();
        ClientUtils.applyRotation(this.server);
    }
    public void handlePostMotion() {
        ClientUtils.applyRotation(this.client);

        active = false;
    }

    @Override
    public void addListeners(Module parentModule) {
        parentModule.addListen(PreMovementPacketEvent.class);
        parentModule.addListen(PostMovementPacketEvent.class);
    }

    @Override
    public void removeListeners(Module parentModule) {
        parentModule.removeListen(PreMovementPacketEvent.class);
        parentModule.removeListen(PostMovementPacketEvent.class);
    }

    private Rotation server;
    private Rotation client;

    private boolean active = false;


    public void setServer(Rotation rotation) {
        this.server = rotation;
    }

    public void setClient(Rotation rotation) {
        this.client = rotation;
    }

    public void lookAtPosServer(Vec3d pos) {
        this.active = true;

        this.server = RotationUtils.getRequiredRotation(pos);
    }
    public void lookAtPosClient(Vec3d pos) {
        ClientUtils.lookAtPos(pos);
    }

    public Rotation getServerRotation() {
        return this.active ? this.server : ClientUtils.getRotation();
    }

    @Override
    public boolean fireEvent(Event event) {
        if (!active) return false;

        switch (event.getClass().getSimpleName()) {
            case "PostMotionEvent": {
                this.handlePostMotion();
                return true;
            }

            case "PreMotionEvent": {
                this.handlePreMotion();
                return true;
            }
        }

        return false;
    }
}
