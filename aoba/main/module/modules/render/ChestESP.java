package aoba.main.module.modules.render;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;

public class ChestESP extends Module {

	public ChestESP() {
		this.setName("ChestESP");
		this.setBind(new KeyBinding("key.chestesp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see Chests with an ESP.");
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
			if(entity instanceof ChestTileEntity) {
				this.getRenderUtils().TileEntityESPBox(entity, 1f, 0.6f, 0);
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
