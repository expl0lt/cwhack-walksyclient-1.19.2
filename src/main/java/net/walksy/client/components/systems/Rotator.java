package net.walksy.client.components.systems;

import net.minecraft.util.math.Vec3d;
import net.walksy.client.WalksyClient;
import net.walksy.client.utils.RotationRot;
import net.walksy.client.utils.RotationUtils;

import java.util.ArrayList;

public class Rotator
{

    public Rotator()
    {
    }

    private static final ArrayList<RotationRot> rotations = new ArrayList<>();
    private static Runnable callback;


    public static void onPlayerTick()
    {
        if (rotations.size() != 0)
        {
            RotationUtils.setRotation(rotations.get(rotations.size() - 1));
            rotations.remove(rotations.size() - 1);
            if (rotations.size() == 0)
                callback.run();
        }
    }

    public void stepToward(Vec3d pos, int steps, Runnable callback)
    {
        stepToward(RotationUtils.getNeededRotations(pos), steps, callback);
    }

    public void stepToward(RotationRot rotation, int steps, Runnable callback)
    {
        rotations.clear();
        float yaw = rotation.getYaw();
        float pitch = rotation.getPitch();
        float stepYaw = (yaw - WalksyClient.getClient().player.getYaw()) / (float) steps;
        float stepPitch = (pitch - WalksyClient.getClient().player.getPitch()) / (float) steps;
        for (int i = 0; i < steps; i++)
        {
            rotations.add(new RotationRot(yaw, rotation.isIgnoreYaw(), pitch, rotation.isIgnorePitch()));
            yaw -= stepYaw;
            pitch -= stepPitch;
        }
        this.callback = callback;
    }
}
