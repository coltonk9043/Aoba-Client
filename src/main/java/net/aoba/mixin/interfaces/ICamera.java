package net.aoba.mixin.interfaces;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface ICamera {
    @Accessor("entity")
    void setFocusedEntity(Entity focusedEntity);

    @Invoker("move")
    void moveCameraBy(float f, float g, float h);

    @Invoker("setPosition")
    void setCameraPos(Vec3 vec);

    @Invoker("setRotation")
    void setCameraRotation(float yaw, float pitch);
}
