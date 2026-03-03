package net.aoba.mixin.interfaces;

import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Cube.class)
public interface IModelPartCube {
    @Accessor("polygons")
    Polygon[] getSides();
}
