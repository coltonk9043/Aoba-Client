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
 * A class to represent a system that manages all the Modules.
 */
package net.aoba.module;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.mojang.logging.LogUtils;

import net.aoba.Aoba;
import net.aoba.api.IAddon;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.module.modules.combat.Aimbot;
import net.aoba.module.modules.combat.AntiInvis;
import net.aoba.module.modules.combat.AntiKnockback;
import net.aoba.module.modules.combat.AutoRespawn;
import net.aoba.module.modules.combat.AutoSoup;
import net.aoba.module.modules.combat.AutoTotem;
import net.aoba.module.modules.combat.BedAura;
import net.aoba.module.modules.combat.BowAimbot;
import net.aoba.module.modules.combat.Criticals;
import net.aoba.module.modules.combat.CrystalAura;
import net.aoba.module.modules.combat.KillAura;
import net.aoba.module.modules.combat.MaceAura;
import net.aoba.module.modules.combat.Nametags;
import net.aoba.module.modules.combat.Reach;
import net.aoba.module.modules.combat.TriggerBot;
import net.aoba.module.modules.misc.AntiCactus;
import net.aoba.module.modules.misc.AntiHunger;
import net.aoba.module.modules.misc.AutoEat;
import net.aoba.module.modules.misc.AutoFish;
import net.aoba.module.modules.misc.AutoShear;
import net.aoba.module.modules.misc.AutoWalk;
import net.aoba.module.modules.misc.DiscordRPCModule;
import net.aoba.module.modules.misc.EXPThrower;
import net.aoba.module.modules.misc.FakePlayer;
import net.aoba.module.modules.misc.FastBreak;
import net.aoba.module.modules.misc.FastPlace;
import net.aoba.module.modules.misc.MCA;
import net.aoba.module.modules.misc.Timer;
import net.aoba.module.modules.misc.XCarry;
import net.aoba.module.modules.movement.ClickTP;
import net.aoba.module.modules.movement.EntityControl;
import net.aoba.module.modules.movement.FastLadder;
import net.aoba.module.modules.movement.Fly;
import net.aoba.module.modules.movement.Freecam;
import net.aoba.module.modules.movement.Glide;
import net.aoba.module.modules.movement.GuiMove;
import net.aoba.module.modules.movement.HighJump;
import net.aoba.module.modules.movement.Jesus;
import net.aoba.module.modules.movement.Jetpack;
import net.aoba.module.modules.movement.NoFall;
import net.aoba.module.modules.movement.NoJumpDelay;
import net.aoba.module.modules.movement.NoSlowdown;
import net.aoba.module.modules.movement.Noclip;
import net.aoba.module.modules.movement.ReverseStep;
import net.aoba.module.modules.movement.Safewalk;
import net.aoba.module.modules.movement.Sneak;
import net.aoba.module.modules.movement.Speed;
import net.aoba.module.modules.movement.Spider;
import net.aoba.module.modules.movement.Sprint;
import net.aoba.module.modules.movement.Step;
import net.aoba.module.modules.movement.Strafe;
import net.aoba.module.modules.render.Breadcrumbs;
import net.aoba.module.modules.render.ChestESP;
import net.aoba.module.modules.render.EntityESP;
import net.aoba.module.modules.render.FocusFps;
import net.aoba.module.modules.render.Fullbright;
import net.aoba.module.modules.render.ItemESP;
import net.aoba.module.modules.render.NoRender;
import net.aoba.module.modules.render.POV;
import net.aoba.module.modules.render.PlayerESP;
import net.aoba.module.modules.render.SpawnerESP;
import net.aoba.module.modules.render.Tooltips;
import net.aoba.module.modules.render.Tracer;
import net.aoba.module.modules.render.Trajectory;
import net.aoba.module.modules.render.XRay;
import net.aoba.module.modules.render.Zoom;
import net.aoba.module.modules.world.AutoFarm;
import net.aoba.module.modules.world.AutoSign;
import net.aoba.module.modules.world.Nuker;
import net.aoba.module.modules.world.Scaffold;
import net.aoba.module.modules.world.Surround;
import net.aoba.module.modules.world.TileBreaker;
import net.aoba.settings.Setting;
import net.aoba.settings.SettingManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil.Key;

public class ModuleManager implements KeyDownListener {
	public ArrayList<Module> modules = new ArrayList<Module>();

	private MinecraftClient mc = MinecraftClient.getInstance();

	// Modules
	public Aimbot aimbot = new Aimbot();
	public AntiCactus anticactus = new AntiCactus();
	public AntiInvis antiinvis = new AntiInvis();
	public AntiKnockback antiknockback = new AntiKnockback();
	public AutoEat autoeat = new AutoEat();
	public AutoFarm autofarm = new AutoFarm();
	public AutoFish autofish = new AutoFish();
	public AntiHunger antihunger = new AntiHunger();
	public AutoShear autoShear = new AutoShear();
	public AutoSign autosign = new AutoSign();
	public AutoSoup autosoup = new AutoSoup();
	public AutoTotem autoTotem = new AutoTotem();
	public AutoRespawn autorespawn = new AutoRespawn();
	public AutoWalk autowalk = new AutoWalk();
	public BedAura bedAura = new BedAura();
	public BowAimbot bowaimbot = new BowAimbot();
	public Breadcrumbs breadcrumbs = new Breadcrumbs();
	public ChestESP chestesp = new ChestESP();
	public Criticals criticals = new Criticals();
	public CrystalAura crystalaura = new CrystalAura();
	public ClickTP clickTP = new ClickTP();
	public DiscordRPCModule discordRPC = new DiscordRPCModule();
	public EntityControl entityControl = new EntityControl();
	public EntityESP entityesp = new EntityESP();
	public EXPThrower expthrower = new EXPThrower();
	public FakePlayer fakeplayer = new FakePlayer();
	public FastLadder fastladder = new FastLadder();
	public FastPlace fastplace = new FastPlace();
	public FastBreak fastbreak = new FastBreak();
	public Fly fly = new Fly();
	public Freecam freecam = new Freecam();
	public Fullbright fullbright = new Fullbright();
	public ItemESP itemesp = new ItemESP();
	public NoRender norender = new NoRender();
	public FocusFps focusfps = new FocusFps();
	public Glide glide = new Glide();
	public GuiMove guimove = new GuiMove();
	public HighJump higherjump = new HighJump();
	public Jesus jesus = new Jesus();
	public Jetpack jetpack = new Jetpack();
	public KillAura killaura = new KillAura();
	public MaceAura maceaura = new MaceAura();
	public MCA mcf = new MCA();
	public Nametags nametags = new Nametags();
	public Noclip noclip = new Noclip();
	public NoFall nofall = new NoFall();
	public NoJumpDelay nojumpdelay = new NoJumpDelay();
	public ReverseStep reverseStep = new ReverseStep();
	public NoSlowdown noslowdown = new NoSlowdown();
	public Nuker nuker = new Nuker();
	public PlayerESP playeresp = new PlayerESP();
	public POV pov = new POV();
	public Reach reach = new Reach();
	public Safewalk safewalk = new Safewalk();
	public Scaffold scaffold = new Scaffold();
	public Sneak sneak = new Sneak();
	public SpawnerESP spawneresp = new SpawnerESP();
	public Speed speed = new Speed();
	public Spider spider = new Spider();
	public Sprint sprint = new Sprint();
	public Step step = new Step();
	public Strafe strafe = new Strafe();
	public Surround surround = new Surround();
	public TileBreaker tilebreaker = new TileBreaker();
	public Timer timer = new Timer();
	public Tooltips tooltips = new Tooltips();
	public Tracer tracer = new Tracer();
	public Trajectory trajectory = new Trajectory();
	public TriggerBot triggerbot = new TriggerBot();
	public XCarry xCarry = new XCarry();
	public XRay xray = new XRay();
	public Zoom zoom = new Zoom();

	public ModuleManager(List<IAddon> addons) {
		try {
			// Attempts to find each field of type Module and add it to the module list.
			for (Field field : ModuleManager.class.getDeclaredFields()) {
				if (!Module.class.isAssignableFrom(field.getType()))
					continue;
				Module module = (Module) field.get(this);
				addModule(module);
			}

			// Gets each Addon and adds their modules to the client.
			addons.stream().filter(Objects::nonNull).forEach(addon -> {
				addon.modules().forEach(module -> {
					addModule(module);
				});
			});
		} catch (Exception e) {
			LogUtils.getLogger().error("Error initializing Aoba modules: " + e.getMessage());
		}

		// Registers all Module settings to the settings manager.
		for (Module module : modules) {
			for (Setting<?> setting : module.getSettings()) {
				SettingManager.registerSetting(setting);
			}
		}

		Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
	}

	public void addModule(Module module) {
		modules.add(module);
	}

	public void disableAll() {
		for (Module module : modules) {
			module.state.setValue(false);
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
	public void onKeyDown(KeyDownEvent event) {
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
