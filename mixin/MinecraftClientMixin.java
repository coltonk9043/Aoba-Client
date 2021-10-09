package net.aoba.mixin;

import net.aoba.Aoba;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	private int itemUseCooldown;
	@Shadow
	private Session session;
	@Shadow
	public ClientWorld world;
	
	public void setSession(Session session)
	{
		this.session = session;
	}
	
	@Inject(at = @At("TAIL"), method = "tick()V")
	public void tick(CallbackInfo info) {
		if (this.world != null) {
			Aoba.getInstance().update();
		}
	}
	
}
