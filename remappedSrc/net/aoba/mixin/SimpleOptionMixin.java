package net.aoba.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.aoba.interfaces.ISimpleOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

@Mixin(SimpleOption.class)
public class SimpleOptionMixin<T> implements ISimpleOption<T>
{
	@Shadow
	T value;
	
	@Shadow
	@Final
	private Consumer<T> changeCallback;
	
	@Override
	public void forceSetValue(T newValue)
	{
		if(!MinecraftClient.getInstance().isRunning())
		{
			value = newValue;
			return;
		}
		
		if(!value.equals(newValue))
		{
			value = newValue;
			changeCallback.accept(value);
		}
	}
}