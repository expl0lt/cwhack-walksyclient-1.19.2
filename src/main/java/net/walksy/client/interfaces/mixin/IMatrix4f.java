package net.walksy.client.interfaces.mixin;

import net.walksy.client.misc.maths.Vec4;
import net.minecraft.util.math.Vec3d;
import net.walksy.client.utils.Vec4d;

public interface IMatrix4f {
    public void multiplyMatrix(Vec4 vec4, Vec4 mmmat4);
    public Vec3d mul(Vec3d vec);

    Vec4d multiply(Vec4d v);

    Vec3d multiply(Vec3d v);
}
