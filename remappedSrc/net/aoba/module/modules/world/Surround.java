package net.aoba.module.modules.world;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.glfw.GLFW;
import com.google.common.collect.Lists;
import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module implements TickListener {

	public static final ArrayList<Block> blocks = Lists.newArrayList(Blocks.OBSIDIAN, Blocks.ENDER_CHEST,
			Blocks.ENCHANTING_TABLE, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.CRYING_OBSIDIAN,
			Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS, Blocks.RESPAWN_ANCHOR);

	public FloatSetting placeHeight;
	public BooleanSetting alignCharacter;
	public BooleanSetting autoDisable;
	public BooleanSetting legit;

	public Surround() {
		super(new KeybindSetting("key.surround", "Surround Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("Surround");
		this.setCategory(Category.World);
		this.setDescription("Surrounds the player with blocks.");
		
		placeHeight = new FloatSetting("surround_height", "Height", "Height that surround walls will go.", 1f, 1f, 3f, 1.0f);
		alignCharacter = new BooleanSetting("surround_align", "Align", "Aligns the character to the nearest block.", false);
		autoDisable = new BooleanSetting("surround_autodisable", "Auto-Disable", "Disables the module when the blocks have finished placing.", false);
		legit = new BooleanSetting("surround_legit", "Legit", "Whether or not to simulate a player looking and clicking to place.", false);
		

		this.addSetting(placeHeight);
		this.addSetting(alignCharacter);
		this.addSetting(autoDisable);
		this.addSetting(legit);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		if(alignCharacter.getValue()) {
			BlockPos blockPos = MC.player.getBlockPos();
			MC.player.updatePosition(blockPos.getX() + 0.5f, MC.player.getY(), blockPos.getZ() + 0.5f);
			MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(blockPos.getX() + 0.5f, MC.player.getY(), blockPos.getZ() + 0.5f, MC.player.isOnGround()));
		}
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void OnUpdate(TickEvent event) {
		int foundBlockSlot = getBlockInventorySlot();
		int oldSlot = MC.player.getInventory().selectedSlot;
		// Disable the module is no block was found in the inventory.
		if(foundBlockSlot == -1) {
			this.setState(false);
			return;
		}else {
			// Change the selected slot and determien which hand it is in.
			MC.player.getInventory().selectedSlot = foundBlockSlot;
			MC.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(foundBlockSlot));
			Hand hand = foundBlockSlot == 40 ? Hand.OFF_HAND : Hand.MAIN_HAND;
			
			// Get the block positions around the player and place the blocks.
			BlockPos playerPosition = MC.player.getBlockPos();
			
			int height = placeHeight.getValue().intValue();
			
			for(int i = 0; i < (height - 1); i++) {
				BlockPos newPos = playerPosition.add(0, i, 0);
				List<BlockPos> placePositions = Lists.newArrayList(newPos.north(), newPos.east(), newPos.south(), newPos.west());
				for(BlockPos pos : placePositions) {
					if(MC.world.getBlockState(pos).isReplaceable()) {
						placeBlock(pos, hand);
					}
				}
			}
			
			
		}
		
		// Return Selected Slot back to original slot.
		MC.player.getInventory().selectedSlot = oldSlot;
		MC.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(oldSlot));
		
		// Disable state is auto disable is enabled.
		if(autoDisable.getValue()) {
			setState(false);
		}
	}
	
	/**
	 * Get the inventory slot containing the blocks.
	 * @return Slot containing item.
	 */
	private int getBlockInventorySlot() {
		for(int i = 0; i < 36; i++)
		{
			ItemStack stack = MC.player.getInventory().getStack(i);
			if(stack != null && blocks.contains(Block.getBlockFromItem(stack.getItem()))) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Places the block at a specific position.
	 * @param pos Position of the block to place.
	 * @param hand Hand to place with.
	 */
	private void placeBlock(BlockPos pos, Hand hand) {
		for (Direction direction : Direction.values()) {
			if (!MC.world.isInBuildLimit(pos.offset(direction)))
				continue;
			
			if(legit.getValue()) {
				MC.player.swingHand(hand);
			}else {
				MC.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
			}
			
			MC.interactionManager.interactBlock(MC.player, hand, new BlockHitResult(Vec3d.ofCenter(pos), direction.getOpposite(), pos.offset(direction), false));
			break;
		}
	}
}
