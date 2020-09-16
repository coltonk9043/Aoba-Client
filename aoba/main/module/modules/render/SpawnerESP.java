package aoba.main.module.modules.render;

import org.lwjgl.glfw.GLFW;

import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;

public class SpawnerESP extends Module {

	public SpawnerESP() {
		this.setName("SpawnerESP");
		this.setBind(new KeyBinding("key.spawneresp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see spawners with an ESP.");
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {

	}

	@Override
	public void onRender() {
		for (TileEntity entity : mc.world.loadedTileEntityList) {
			if(entity instanceof MobSpawnerTileEntity) {
				this.getRenderUtils().TileEntityESPBox(entity, 0.0f, 1.0f, 0.0f);
			}
		}
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {
		
	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {
		
		
	}

}