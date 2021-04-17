package aoba.main.module.modules.combat;

import org.lwjgl.glfw.GLFW;

import aoba.main.module.Module;
import aoba.main.module.Module.Category;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class CrystalAura extends Module {
	private float radius = 10.0f;

	public CrystalAura() {
		this.setName("CrystalAura");
		this.setBind(new KeyBinding("key.crystalaura", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Attacks anything within your personal space.");
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
		for (PlayerEntity player : mc.world.getPlayers()) {
			if (player == mc.player || mc.player.getDistance(player) > this.radius) {
				continue;
			}
			BlockPos entityPos = player.getPosition();
			BlockPos check = entityPos.add(0, -1, 0);
			BlockState bs = mc.world.getBlockState(check);
			Block block = bs.getBlock();
			if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK)
				continue;

			System.out.println(player.getName().getString() + ": " + bs.getBlock().getTranslatedName().getString());
			for (int slot = 0; slot < 9; slot++) {
				Item item = mc.player.inventory.getStackInSlot(slot).getItem();
				if (item == Items.END_CRYSTAL) {
					mc.player.inventory.currentItem = slot;
					break;
				}
			}
			BlockRayTraceResult rayTrace = new BlockRayTraceResult(false, new Vector3d(0, 0, 0), Direction.UP, check,
					false);
			this.mc.getConnection().sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, rayTrace));
		}

		// HIT THING
		for (Entity entity : mc.world.getAllEntities()) {
			if (mc.player.getDistance(entity) < radius) {
				if (entity instanceof EnderCrystalEntity) {
					mc.playerController.attackEntity(mc.player, entity);
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