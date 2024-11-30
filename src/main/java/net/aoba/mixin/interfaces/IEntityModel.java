package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.model.EntityModel;

@Mixin(EntityModel.class)
public interface IEntityModel {
	/*
	 * @Invoker("getHeadParts") Iterable<ModelPart> invokeGetHeadParts();
	 * 
	 * @Invoker("getBodyParts") Iterable<ModelPart> invokeGetBodyParts();
	 */
}
