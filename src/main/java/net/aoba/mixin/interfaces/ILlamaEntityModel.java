package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.LlamaEntityModel;

@Mixin(LlamaEntityModel.class)
public interface ILlamaEntityModel {
    @Accessor("head")
    ModelPart getHead();
    
    @Accessor("body")
    ModelPart getBody();
    
    @Accessor("rightHindLeg")
    ModelPart getRightHindLeg();
    
    @Accessor("leftHindLeg")
    ModelPart getLeftHindLeg();
    
    @Accessor("rightFrontLeg")
    ModelPart getRightFrontLeg();
    
    @Accessor("leftFrontLeg")
    ModelPart getLeftFrontLeg();
    
    @Accessor("rightChest")
    ModelPart getRightChest();
    
    @Accessor("leftChest")
    ModelPart getLeftChest();
}
