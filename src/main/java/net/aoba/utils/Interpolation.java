package net.aoba.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

import static net.aoba.AobaClient.MC;

public class Interpolation
{
    public static Vec3d interpolateEntity(Entity entity)
    {
        double x = interpolateLastTickPos(entity.getX(), entity.prevX);
        double y = interpolateLastTickPos(entity.getY(), entity.prevY);
        double z = interpolateLastTickPos(entity.getZ(), entity.prevZ);
        return new Vec3d(x, y, z);
    }

    public static double interpolateLastTickPos(double pos, double lastPos)
    {
        return lastPos + (pos - lastPos) * MC.getRenderTickCounter().getTickDelta(false);
    }
}
