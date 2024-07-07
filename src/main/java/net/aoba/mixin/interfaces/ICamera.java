package net.aoba.mixin.interfaces;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface ICamera {
    @Accessor("focusedEntity")
    void setFocusedEntity(Entity focusedEntity);

    @Invoker("moveBy")
    void moveCameraBy(float f, float g, float h);

    @Invoker("setPos")
    void setCameraPos(Vec3d vec);

    @Invoker("setRotation")
    void setCameraRotation(float yaw, float pitch);
}
