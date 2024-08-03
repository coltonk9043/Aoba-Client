package net.aoba.mixin.interfaces;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;

@Mixin(ModelPart.class)
public interface IModelPart {
    @Accessor("cuboids")
    List<Cuboid> getCuboids();
    
    @Accessor("children")
    Map<String, ModelPart> getChildren();
}
