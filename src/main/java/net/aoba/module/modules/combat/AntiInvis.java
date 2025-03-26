/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import net.aoba.module.Category;
import net.aoba.module.Module;

public class AntiInvis extends Module {

    public AntiInvis() {
    	super("AntiInvis");
        setCategory(Category.of("Combat"));
        setDescription("Reveals players who are invisible.");
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
}