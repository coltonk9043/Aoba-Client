package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.RabbitEntityModel;

@Mixin(RabbitEntityModel.class)
public interface IRabbitEntityModel {
    @Accessor("leftHindLeg")
    ModelPart getLeftHindLeg();
    
    @Accessor("rightHindLeg")
    ModelPart getRightHindLeg();
    
    @Accessor("leftHaunch")
    ModelPart getLeftHaunch();
    
    @Accessor("rightHaunch")
    ModelPart getRightHaunch();
    
    @Accessor("body")
    ModelPart getBody();
    
    @Accessor("leftFrontLeg")
    ModelPart getLeftFrontLeg();
    
    @Accessor("rightFrontLeg")
    ModelPart getRightFrontLeg();
    
    @Accessor("head")
    ModelPart getHead();
    
    @Accessor("rightEar")
    ModelPart getRightEar();
    
    @Accessor("leftEar")
    ModelPart getLeftEar();
    
    @Accessor("tail")
    ModelPart getTail();
    
    @Accessor("nose")
    ModelPart getNose();
}
