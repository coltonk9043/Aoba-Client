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
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;

public class Nametags extends Module {

    private final FloatSetting scale = FloatSetting.builder()
    		.id("nametags_scale")
    		.displayName("Scale")
    		.description("Scale of the NameTags")
    		.defaultValue(1.25f)
    		.minValue(0f)
    		.maxValue(5f)
    		.step(0.25f)
    		.build();

    private final BooleanSetting onlyPlayers = BooleanSetting.builder()
    		.id("nametags_onlyPlayers")
    		.displayName("Only Players")
    		.description("Whether Nametags are only enlarged for players.")
    		.defaultValue(false)
    		.build();

    private final BooleanSetting alwaysVisible = BooleanSetting.builder()
    		.id("nametags_alwaysVisible")
    		.displayName("Always Visible")
    		.description("Whether Nametags will always be displayed.")
    		.defaultValue(false)
    		.build();

    public Nametags() {
    	super("Nametags");

        setCategory(Category.of("Combat"));
        setDescription("Scales the nametags to be larger.");
        
        addSetting(scale);
        addSetting(onlyPlayers);
        addSetting(alwaysVisible);
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

    public double getNametagScale() {
        return scale.getValue();
    }

    public boolean getPlayersOnly() {
        return onlyPlayers.getValue();
    }

    public boolean getAlwaysVisible() {
        return alwaysVisible.getValue();
    }
}