package net.aoba.mixin;

import net.aoba.Aoba;
import net.aoba.interfaces.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraftClient{
	@Shadow
	private int itemUseCooldown;
	@Shadow
	@Final
	private Session session;
	@Shadow
	public ClientWorld world;

	private Session aobaSession;
	
	@Override
	public void setSession(Session session) {
		aobaSession = session;
	}

	@Inject(at = @At("TAIL"), method = "tick()V")
	public void tick(CallbackInfo info) {
		if (this.world != null) {
			Aoba.getInstance().update();
		}
	}

	@Inject(at = {@At("HEAD")}, method = {"getSession()Lnet/minecraft/client/util/Session;"}, cancellable = true)
		private void onGetSession(CallbackInfoReturnable<Session> cir)
		{
			if(aobaSession == null) return;
			cir.setReturnValue(aobaSession);
		}
	
	@Redirect(at = @At(value = "FIELD",target = "Lnet/minecraft/client/MinecraftClient;session:Lnet/minecraft/client/util/Session;",opcode = Opcodes.GETFIELD,ordinal = 0),method = {"getSessionProperties()Lcom/mojang/authlib/properties/PropertyMap;"})
		private Session getSessionForSessionProperties(MinecraftClient mc)
		{
			if(aobaSession != null)return aobaSession;
			return session;
		}
	
	@Inject(at = {@At(value = "HEAD") }, method = {"doAttack()Z"}, cancellable = true)
	private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
		if (Aoba.getInstance().hm.isClickGuiOpen()) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
	
	@Inject(at = {@At(value = "HEAD")}, method = {"close()V"})
	private void onClose(CallbackInfo ci) {
		try {
			Aoba.getInstance().endClient();
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	@Override
	public int getRightClickDelay()
	{
		return itemUseCooldown;
	}
	
	@Override
	public void setRightClickDelay(int delay)
	{
		this.itemUseCooldown = delay;
	}
}
