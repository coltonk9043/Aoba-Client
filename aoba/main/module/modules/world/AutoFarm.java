package aoba.main.module.modules.world;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import aoba.main.misc.Utils;
import aoba.main.module.Module;
import aoba.main.module.Module.Category;
import aoba.main.settings.SliderSetting;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket.Action;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class AutoFarm extends Module {
	private Minecraft mc;
	private SliderSetting radius;

	public AutoFarm() {
		this.setName("AutoFarm");
		this.setBind(new KeyBinding("key.autofarm", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.World);
		this.setDescription("Destroys blocks that can be instantly broken around the player.");
		this.radius = new SliderSetting("Radius", "autofarm_radius", 5f, 0f, 15f, 1f);
		this.addSetting(radius);
	}

	public void setRadius(int radius) {
		this.radius.setValue(radius);
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
		mc = Minecraft.getInstance();
		int rad = radius.getValueInt();
		for (int x = -rad; x < rad; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos((int) mc.player.getPosX() + x, (int) mc.player.getPosY() + y,
							(int) mc.player.getPosZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();
					BlockState blockstate = mc.world.getBlockState(blockpos);
					if (block instanceof CropsBlock) {
						CropsBlock crop = (CropsBlock) block;
						if (!crop.canGrow(mc.world, blockpos, blockstate, true)) {
							mc.player.connection.sendPacket(
									new CPlayerDiggingPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
							mc.player.connection.sendPacket(
									new CPlayerDiggingPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
						}else {
							boolean b = false;
							for(int i = 0; i< 9; i++) {
								ItemStack stack = mc.player.inventory.getStackInSlot(i);
								if(stack.getItem() == Items.BONE_MEAL) {
							    	mc.player.inventory.currentItem = i;
							    	b = true;
							    	break;
							    }
							}
							if(b) {
								BlockRayTraceResult rayTrace = new BlockRayTraceResult(false, new Vector3d(0,0,0), Direction.UP, blockpos, false);
								this.mc.getConnection().sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, rayTrace));
							}
						}
					}else if (block instanceof FarmlandBlock) {
						BlockPos blockAbovePos = new BlockPos((int) mc.player.getPosX() + x, (int) mc.player.getPosY() + y + 1,
								(int) mc.player.getPosZ() + z);
						Block blockAbove = mc.world.getBlockState(blockAbovePos).getBlock();
						if(blockAbove instanceof AirBlock) {
							boolean b = false;
							for(int i = 0; i< 9; i++) {
								ItemStack stack = mc.player.inventory.getStackInSlot(i);
								if(Utils.isPlantable(stack)) {
							    	mc.player.inventory.currentItem = i;
							    	b = true;
							    	break;
							    }
							}
							if(b) {
								BlockRayTraceResult rayTrace = new BlockRayTraceResult(false, new Vector3d(0,0,0), Direction.UP, blockpos, false);
								this.mc.getConnection().sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, rayTrace));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onRender() {
		
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {

	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {

	}
}