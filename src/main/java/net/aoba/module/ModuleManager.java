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
 * A class to represent a system that manages all of the Modules.
 */
package net.aoba.module;

import com.mojang.logging.LogUtils;
import net.aoba.Aoba;
import net.aoba.api.IAddon;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.module.modules.combat.*;
import net.aoba.module.modules.misc.*;
import net.aoba.module.modules.movement.*;
import net.aoba.module.modules.render.*;
import net.aoba.module.modules.world.*;
import net.aoba.settings.Setting;
import net.aoba.settings.SettingManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModuleManager implements KeyDownListener {
    public ArrayList<Module> modules = new ArrayList<Module>();

    private MinecraftClient mc = MinecraftClient.getInstance();

    //Modules
    public Module aimbot = new Aimbot();
    public Module anticactus = new AntiCactus();
    public Module antiinvis = new AntiInvis();
    public Module antiknockback = new AntiKnockback();
    public Module antikick = new AntiKick();
    public Module autoeat = new AutoEat();
    public Module autofarm = new AutoFarm();
    public Module autofish = new AutoFish();
    public Module autosign = new AutoSign();
    public Module autosoup = new AutoSoup();
    public Module autoTotem = new AutoTotem();
    public Module autorespawn = new AutoRespawn();
    public Module autowalk = new AutoWalk();
    public Module bowaimbot = new BowAimbot();
    public Module breadcrumbs = new Breadcrumbs();
    public Module chestesp = new ChestESP();
    public Module criticals = new Criticals();
    public Module crystalaura = new CrystalAura();
    public Module clickTP = new ClickTP();
    public Module entityesp = new EntityESP();
    public Module fastladder = new FastLadder();
    public Module fastplace = new FastPlace();
    public Module fastbreak = new FastBreak();
    public Module fly = new Fly();
    public Module freecam = new Freecam();
    public Module fullbright = new Fullbright();
    public Module itemesp = new ItemESP();
    public Module glide = new Glide();
    public Module higherjump = new HighJump();
    public Module jesus = new Jesus();
    public Module jetpack = new Jetpack();
    public Module killaura = new KillAura();
    public Module maceaura = new MaceAura();
    public Module nametags = new Nametags();
    public Module noclip = new Noclip();
    public Module nofall = new NoFall();
    public Module nojumpdelay = new NoJumpDelay();
    public Module nooverlay = new NoOverlay();
    public Module noslowdown = new NoSlowdown();
    public Module nuker = new Nuker();
    public Module playeresp = new PlayerESP();
    public Module pov = new POV();
    public Module reach = new Reach();
    public Module safewalk = new Safewalk();
    public Module sneak = new Sneak();
    public Module spawneresp = new SpawnerESP();
    public Module speed = new Speed();
    public Module spider = new Spider();
    public Module sprint = new Sprint();
    public Module step = new Step();
    public Module strafe = new Strafe();
    public Module surround = new Surround();
    public Module tilebreaker = new TileBreaker();
    public Module timer = new Timer();
    public Module tracer = new Tracer();
    public Module trajectory = new Trajectory();
    public Module triggerbot = new TriggerBot();
    public Module xray = new XRay();
    public Module zoom = new Zoom();

    public ModuleManager(List<IAddon> addons) {
        addModule(aimbot);
        addModule(anticactus);
        addModule(antiinvis);
        addModule(antiknockback);
        addModule(antikick);
        addModule(autoeat);
        addModule(autofarm);
        addModule(autofish);
        addModule(autosign);
        addModule(autosoup);
        addModule(autoTotem);
        addModule(autorespawn);
        addModule(autowalk);
        addModule(bowaimbot);
        addModule(breadcrumbs);
        addModule(chestesp);
        addModule(criticals);
        addModule(crystalaura);
        addModule(clickTP);
        addModule(entityesp);
        addModule(fastladder);
        addModule(fastplace);
        addModule(fastbreak);
        addModule(fly);
        addModule(freecam);
        addModule(fullbright);
        addModule(glide);
        addModule(higherjump);
        addModule(itemesp);
        addModule(jesus);
        addModule(jetpack);
        addModule(killaura);
        addModule(maceaura);
        //addModule(nametags);
        addModule(noclip);
        addModule(nofall);
        addModule(nojumpdelay);
        addModule(nooverlay);
        addModule(noslowdown);
        addModule(nuker);
        addModule(playeresp);
        addModule(pov);
        addModule(reach);
        addModule(safewalk);
        addModule(sneak);
        addModule(spawneresp);
        addModule(speed);
        addModule(spider);
        addModule(sprint);
        addModule(step);
        addModule(strafe);
        addModule(surround);
        addModule(tilebreaker);
        addModule(timer);
        addModule(triggerbot);
        addModule(tracer);
        addModule(trajectory);
        addModule(xray);
        addModule(zoom);

        addons.stream().filter(Objects::nonNull).forEach(addon -> {
            try {
                addon.modules().forEach(module -> {
                    try {
                        addModule(module);
                        LogUtils.getLogger().info("[Aoba] Successfully added module: " + module.getClass().getName() + " from addon: " + addon.getClass().getName());
                    } catch (Exception e) {
                        LogUtils.getLogger().error("Error adding module: " + module.getClass().getName() + " from addon: " + addon.getClass().getName(), e);
                    }
                });
            } catch (Exception e) {
                LogUtils.getLogger().error("Error processing modules from addon: " + addon.getClass().getName(), e);
            }
        });

        for (Module module : modules) {
            for (Setting<?> setting : module.getSettings()) {
                SettingManager.registerSetting(setting, Aoba.getInstance().settingManager.modulesContainer);
            }
        }

        Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
    }

    public void addModule(Module module) {
        modules.add(module);
    }

    public void disableAll() {
        for (Module module : modules) {
            module.setState(false);
        }
    }

    public Module getModuleByName(String string) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(string)) {
                return module;
            }
        }
        return null;
    }

    @Override
    public void OnKeyDown(KeyDownEvent event) {
        if (mc.currentScreen == null) {
            for (Module module : modules) {
                Key binding = module.getBind().getValue();
                if (binding.getCode() == event.GetKey()) {
                    module.toggle();
                }
            }
        }
    }
}
