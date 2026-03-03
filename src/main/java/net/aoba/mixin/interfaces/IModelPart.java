package net.aoba.mixin.interfaces;

import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.class)
public interface IModelPart {
    @Accessor("cubes")
    List<Cube> getCuboids();
    
    @Accessor("children")
    Map<String, ModelPart> getChildren();
}
