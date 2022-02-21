package net.aoba.module;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

import net.aoba.misc.RenderUtils;
import net.aoba.module.modules.combat.*;
import net.aoba.module.modules.misc.*;
import net.aoba.module.modules.movement.*;
import net.aoba.module.modules.render.*;
import net.aoba.module.modules.world.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class ModuleManager {
	public ArrayList<Module> modules = new ArrayList<Module>();
	
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
	public Module reach = new Reach();
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
		addModule(reach);
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
			if(module.getBind().wasPressed()) {
				module.toggle();
				module.getBind().setPressed(false);
			}
			if(module.getState()) {
				module.onUpdate();
			}
		}
	}
	
	public void render(MatrixStack matrixStack) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		
		
		matrixStack.push();
		RenderUtils.applyRenderOffset(matrixStack);
		for(Module module : modules) {
			if(module.getState()) {
				module.onRender(matrixStack, MinecraftClient.getInstance().getTickDelta());
			}
		}
		matrixStack.pop();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	public void sendPacket(Packet<?> packet) {
		for(Module module : modules) {
			if(module.getState()) {
				module.onSendPacket(packet);
			}
		}
	}
	
	public void recievePacket(Packet<?> packet) {
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
