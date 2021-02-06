package aoba.main.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public class FakePlayerEntity extends AbstractClientPlayerEntity {

	public FakePlayerEntity() {
		super(Minecraft.getInstance().world, Minecraft.getInstance().player.getGameProfile());
		ClientPlayerEntity player = Minecraft.getInstance().player;
		this.setPositionAndRotation(player.getPosX(), player.getPosY(), player.getPosZ(),
				player.getYaw(Minecraft.getInstance().getRenderPartialTicks()),
				player.getPitch(Minecraft.getInstance().getRenderPartialTicks()));
		this.inventory = player.inventory;
	}

	public void despawn() {
		this.removed = true;
	}
}
