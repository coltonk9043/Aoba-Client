/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class GuiMove extends Module implements TickListener {
	public GuiMove() {
		super("GuiMove");
		setCategory(Category.of("Movement"));
		setDescription("Lets the player move while inside of menus using arrow keys..");

		setDetectable(AntiCheat.Karhu);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		if (MC.currentScreen != null && !(MC.currentScreen instanceof ChatScreen)) {
			for (KeyBinding k : new KeyBinding[] { MC.options.forwardKey, MC.options.backKey, MC.options.leftKey,
					MC.options.rightKey, MC.options.jumpKey, MC.options.sprintKey })
				k.setPressed(isKeyPressed(InputUtil.fromTranslationKey(k.getBoundKeyTranslationKey()).getCode()));

			float deltaX = 0;
			float deltaY = 0;

			if (isKeyPressed(264))
				deltaY += 10f;

			if (isKeyPressed(265))
				deltaY -= 10f;

			if (isKeyPressed(262))
				deltaX += 10f;

			if (isKeyPressed(263))
				deltaX -= 10f;

			if (deltaX != 0 || deltaY != 0)
				MC.player.changeLookDirection(deltaX, deltaY);
		}
	}

}
