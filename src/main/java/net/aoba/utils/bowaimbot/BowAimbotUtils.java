package net.aoba.utils.bowaimbot;

import net.aoba.AobaClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BowAimbotUtils {
    private static final MinecraftClient MC = AobaClient.MC;

    public static Vec3d getEyesPos() {
        ClientPlayerEntity player = MC.player;
        float eyeHeight = player.getEyeHeight(player.getPose());
        return player.getPos().add(0, eyeHeight, 0);
    }

    public static Vec3d getClientLookVec(float partialTicks) {
        float yaw = MC.player.getYaw(partialTicks);
        float pitch = MC.player.getPitch(partialTicks);
        return new BowAimbotRotation(yaw, pitch).toLookVec();
    }

    public static BowAimbotRotation getNeededRotations(Vec3d vec) {
        Vec3d eyes = getEyesPos();

        double diffX = vec.x - eyes.x;
        double diffZ = vec.z - eyes.z;
        double yaw = Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;

        double diffY = vec.y - eyes.y;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        double pitch = -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return BowAimbotRotation.wrapped((float) yaw, (float) pitch);
    }

    public static double getAngleToLookVec(Vec3d vec) {
        ClientPlayerEntity player = MC.player;
        BowAimbotRotation current = new BowAimbotRotation(player.getYaw(), player.getPitch());
        BowAimbotRotation needed = getNeededRotations(vec);
        return current.getAngleTo(needed);
    }

    public static float limitAngleChange(float current, float intended,
                                         float maxChange) {
        float currentWrapped = MathHelper.wrapDegrees(current);
        float intendedWrapped = MathHelper.wrapDegrees(intended);

        float change = MathHelper.wrapDegrees(intendedWrapped - currentWrapped);
        change = MathHelper.clamp(change, -maxChange, maxChange);

        return current + change;
    }
}
