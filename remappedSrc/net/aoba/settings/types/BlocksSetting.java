/*
* Aoba Hacked Client
* Copyright (C) 2019-2024 coltonk9043
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.aoba.settings.types;

import java.util.HashSet;
import net.aoba.settings.Setting;
import net.minecraft.block.Block;

public class BlocksSetting extends Setting<HashSet<Block>> {

	public BlocksSetting(String ID, String description, HashSet<Block> default_value) {
		super(ID, description, default_value);
		type = TYPE.BLOCKS;
	}

	public BlocksSetting(String ID, String displayName, String description, HashSet<Block> default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.BLOCKS;
	}
	
	@Override
	protected boolean isValueValid(HashSet<Block> value) {
		return true;
	}
}
