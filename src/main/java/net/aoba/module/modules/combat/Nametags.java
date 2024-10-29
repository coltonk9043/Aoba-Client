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

/**
 * Nametags Module
 */
package net.aoba.module.modules.combat;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;

public class Nametags extends Module {

    private FloatSetting scale = FloatSetting.builder()
    		.id("nametags_scale")
    		.displayName("Scale")
    		.description("Scale of the NameTags")
    		.defaultValue(1.25f)
    		.minValue(0f)
    		.maxValue(5f)
    		.step(0.25f)
    		.build();
    
    private BooleanSetting onlyPlayers = BooleanSetting.builder()
    		.id("nametags_onlyPlayers")
    		.displayName("Only Players")
    		.description("Whether Nametags are only enlarged for players.")
    		.defaultValue(false)
    		.build();
    
    private BooleanSetting alwaysVisible = BooleanSetting.builder()
    		.id("nametags_alwaysVisible")
    		.displayName("Always Visible")
    		.description("Whether Nametags will always be displayed.")
    		.defaultValue(false)
    		.build();

    public Nametags() {
    	super("Nametags");

        this.setCategory(Category.of("Combat"));
        this.setDescription("Scales the nametags to be larger.");
        
        this.addSetting(scale);
        this.addSetting(onlyPlayers);
        this.addSetting(alwaysVisible);
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
        return this.scale.getValue();
    }

    public boolean getPlayersOnly() {
        return this.onlyPlayers.getValue();
    }

    public boolean getAlwaysVisible() {
        return this.alwaysVisible.getValue();
    }
}