package net.aoba.settings.types;

import java.util.List;
import net.aoba.settings.Setting;
import net.minecraft.block.Block;

/**
 * 
 */
public class BlocksSetting extends Setting<List<Block>> {

	public BlocksSetting(String ID, String description, List<Block> default_value) {
		super(ID, description, default_value);
		type = TYPE.BLOCKS;
	}

	public BlocksSetting(String ID, String displayName, String description, List<Block> default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.BLOCKS;
	}
	
	@Override
	protected boolean isValueValid(List<Block> value) {
		return true;
	}
}
