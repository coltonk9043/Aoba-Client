package aoba.main.module;

import java.util.ArrayList;
import aoba.main.misc.RenderUtils;
import aoba.main.module.modules.combat.*;
import aoba.main.module.modules.misc.*;
import aoba.main.module.modules.movement.*;
import aoba.main.module.modules.render.*;
import aoba.main.module.modules.world.*;
import net.minecraft.network.IPacket;

public class ModuleManager {
	public ArrayList<Module> modules = new ArrayList<Module>();
	private RenderUtils renderUtils = new RenderUtils();
	
	//Modules
	public Module aimbot = new Aimbot();
	public Module anticactus = new AntiCactus();
	public Module antiinvis = new AntiInvis();
	public Module antiknockback = new AntiKnockback();
	public Module autoeat = new AutoEat();
	public Module autofarm = new AutoFarm();
	public Module autofish = new AutoFish();
	public Module autosign = new AutoSign();
	public Module autosoup = new AutoSoup();
	public Module autorespawn = new AutoRespawn();
	public Module autowalk = new AutoWalk();
	public Module breadcrumbs = new Breadcrumbs();
	public Module chestesp = new ChestESP();
	public Module criticals = new Criticals();
	public Module crystalaura = new CrystalAura();
	public Module entityesp = new EntityESP();
	public Module fastplace = new FastPlace();
	public Module fastbreak = new FastBreak();
	public Module fly = new Fly();
	public Module freecam = new Freecam();
	public Module fullbright = new Fullbright();
	public Module itemesp = new ItemESP();
	public Module glide = new Glide();
	public Module jesus = new Jesus();
	public Module killaura = new KillAura();
	public Module noclip = new Noclip();
	public Module nofall = new NoFall();
	public Module nooverlay = new NoOverlay();
	public Module noslowdown = new NoSlowdown();
	public Module nuker = new Nuker();
	public Module playeresp = new PlayerESP();
	public Module pov = new POV();
	public Module safewalk = new Safewalk();
	public Module sneak = new Sneak();
	public Module spawneresp = new SpawnerESP();
	public Module spider = new Spider();
	public Module sprint = new Sprint();
	public Module step = new Step();
	public Module tilebreaker = new TileBreaker();
	public Module timer = new Timer();
	public Module tracer = new Tracer();
	public Module trajectory = new Trajectory();
	public Module xray = new XRay();
	
	public ModuleManager() {
		// Look at all these modules!
		addModule(aimbot);
		addModule(anticactus);
		addModule(antiinvis);
		addModule(antiknockback);
		addModule(autoeat);
		addModule(autofarm);
		addModule(autofish);
		addModule(autosign);
		addModule(autosoup);
		addModule(autorespawn);
		addModule(autowalk);
		addModule(breadcrumbs);
		addModule(chestesp);
		addModule(criticals);
		addModule(crystalaura);
		addModule(entityesp);
		addModule(fastplace);
		addModule(fastbreak);
		addModule(fly);
		addModule(freecam);
		addModule(fullbright);
		addModule(glide);
		addModule(itemesp);
		addModule(jesus);
		addModule(killaura);
		addModule(noclip);
		addModule(nofall);
		addModule(nooverlay);
		addModule(noslowdown);
		addModule(nuker);
		addModule(playeresp);
		addModule(pov);
		addModule(safewalk);
		addModule(sneak);
		addModule(spawneresp);
		addModule(spider);
		addModule(sprint);
		addModule(step);
		addModule(tilebreaker);
		addModule(timer);
		addModule(tracer);
		addModule(trajectory);
		addModule(xray);
	}
	
	public void update() {
		for(Module module : modules) {
			if(module.getBind().isPressed()) {
				module.toggle();
			}
			if(module.getState()) {
				module.onUpdate();
			}
		}
	}
	
	public void render() {
		renderUtils.applyRenderOffset();
		for(Module module : modules) {
			if(module.getState()) {
				module.onRender();
			}
		}
	}
	
	public void sendPacket(IPacket<?> packet) {
		for(Module module : modules) {
			if(module.getState()) {
				module.onSendPacket(packet);
			}
		}
	}
	
	public void recievePacket(IPacket<?> packet) {
		for(Module module : modules) {
			if(module.getState()) {
				module.onReceivePacket(packet);
			}
		}
	}
	
	public void addModule(Module module) {
		modules.add(module);
	}
	
	public void disableAll() {
		for(Module module : modules) {
			module.setState(false);
		}
	}
	
	public Module getModuleByName(String string) {
		for(Module module : modules) {
			if(module.getName().equalsIgnoreCase(string)) {
				return module;
			}
		}
		return null;
	}
}
