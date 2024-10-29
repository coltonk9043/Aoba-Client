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

package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.cmd.InvalidSyntaxException;
import net.aoba.module.modules.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class CmdXRay extends Command {

	public CmdXRay() {
		super("xray", "Allows the player to see certain blocks through walls", "[toggle/block] [value]");
	}

	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {

		XRay module = (XRay) Aoba.getInstance().moduleManager.xray;

		switch (parameters[0]) {
		case "toggle":
			if (parameters.length != 2)
				throw new InvalidSyntaxException(this);

			String state = parameters[1].toLowerCase();
			if (state.equals("on")) {
				module.state.setValue(true);
				CommandManager.sendChatMessage("XRay toggled ON");
			} else if (state.equals("off")) {
				module.state.setValue(false);
				CommandManager.sendChatMessage("XRay toggled OFF");
			} else {
				CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
			}
			break;
		case "block":
			if (parameters.length != 3)
				throw new InvalidSyntaxException(this);
			switch (parameters[1]) {
			case "add":
				String block1 = parameters[2].toLowerCase();
				Block tempBlock1;
				try {
					tempBlock1 = Registries.BLOCK.get(Identifier.of(block1));
				} catch (InvalidIdentifierException e) {
					CommandManager.sendChatMessage("Block " + parameters[2] + " could not be found.");
					return;
				}

				module.blocks.getValue().add(tempBlock1);
				mc.worldRenderer.reload();
				break;
			case "remove":
				String block2 = parameters[2].toLowerCase();
				Block tempBlock2;
				try {
					tempBlock2 = Registries.BLOCK.get(Identifier.of(block2));
				} catch (InvalidIdentifierException e) {
					CommandManager.sendChatMessage("Block " + parameters[2] + " could not be found.");
					return;
				}

				module.blocks.getValue().remove(tempBlock2);
				mc.worldRenderer.reload();
				break;
			case "list":
				StringBuilder blockList = new StringBuilder();
				for (Block block : module.blocks.getValue()) {
					blockList.append(block.getName().getString()).append(", ");
				}
				blockList = new StringBuilder(blockList.substring(0, blockList.length() - 2));
				CommandManager.sendChatMessage("Block List: " + blockList);
				break;
			}
			break;
		default:
			throw new InvalidSyntaxException(this);
		}
	}

	@Override
	public String[] getAutocorrect(String previousParameter) {
		switch (previousParameter) {
		case "toggle":
			return new String[] { "on", "off" };
		case "block":
			return new String[] { "add", "remove" };
		case "add":
			String[] blockNames = new String[Registries.BLOCK.size()];
			for (int i = 0; i < Registries.BLOCK.size(); i++) {
				blockNames[i] = Registries.BLOCK.get(i).getTranslationKey();
			}
			return blockNames;
		case "remove":
			return new String[] { "xray", "delete" };
		default:
			return new String[] { "toggle", "block" };
		}
	}
}
