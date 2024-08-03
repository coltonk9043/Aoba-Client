package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.model.ModelPart.Quad;

@Mixin(Cuboid.class)
public interface ICuboid {
    @Accessor("sides")
    Quad[] getSides();
}
