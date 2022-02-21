package net.aoba.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;

public class FakePlayerEntity extends AbstractClientPlayerEntity {

	public FakePlayerEntity() {
		super(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getGameProfile());
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		this.setPos(player.getPos().x, player.getPos().y, player.getPos().z);
		this.setRotation(player.getYaw(MinecraftClient.getInstance().getTickDelta()),
				player.getPitch(MinecraftClient.getInstance().getTickDelta()));
		//this.inventory = player.getInventory();
	}

	public void despawn() {
		this.remove(RemovalReason.DISCARDED);
	}
}
