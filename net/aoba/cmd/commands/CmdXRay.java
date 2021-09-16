package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class CmdXRay extends Command {

	public CmdXRay() {
		this.command = "xray";
		this.description = "Allows the player to see certain blocks through walls";
	}

	@Override
	public void command(String[] parameters) {
		XRay module = (XRay) Aoba.getInstance().mm.xray;
		if (parameters.length >= 2 && parameters.length <= 3) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("XRay toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("XRay toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			case "block":
				switch (parameters[1]) {
				case "add":
					String block1 = parameters[2].toLowerCase();
					// TODO MIXIN FIX FIND BLOCK BY NAME
					//Block tempBlock1 = Blocks.getBlockByName(block1);
					
					XRay.blocks.add(Blocks.AIR);
					mc.worldRenderer.reload();
					break;
				case "remove":
					String block2 = parameters[2].toLowerCase();
					//Block tempBlock2 = Blocks.getBlockByName(block2);
					XRay.blocks.remove(Blocks.AIR);
					mc.worldRenderer.reload();
					break;
				case "list":
					String blockList = "";
					for(Block block : XRay.blocks) {
						blockList += block.getName().getString() + ", ";
					}
					blockList = blockList.substring(0, blockList.length() - 2);
					CommandManager.sendChatMessage("Block List: " + blockList);
					break;
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba xray [toggle] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba xray [toggle] [value]");
		}
	}
}
