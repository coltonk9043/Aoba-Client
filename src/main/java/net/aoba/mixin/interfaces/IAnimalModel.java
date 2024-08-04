package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;

@Mixin(AnimalModel.class)
public interface IAnimalModel {
    @Invoker("getHeadParts")
    Iterable<ModelPart> invokeGetHeadParts();
    
    @Invoker("getBodyParts")
    Iterable<ModelPart> invokeGetBodyParts();
}
