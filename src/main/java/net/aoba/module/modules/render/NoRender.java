package net.aoba.module.modules.render;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class NoRender extends Module {
    private BooleanSetting noEatParticles = new BooleanSetting("norender_eat_particles", "Eating Particles", "Does not render eating particles", false);
    private BooleanSetting noTotemAnimation = new BooleanSetting("norender_totem_anim", "Totem Animation", "Does not render the totem floating animation.", false);
    private BooleanSetting noVignette = new BooleanSetting("norender_vignette", "Vignette", "Does not render the Minecraft vignette.", false);
    private BooleanSetting noPumpkinOverlay = new BooleanSetting("norender_pumpkin", "Pumpkin Overlay", "Does not render the pumpkin overlay when you are wearing one.", false);
    private BooleanSetting noFireOverlay = new BooleanSetting("norender_fire", "Fire Overlay", "Does not render the overlay when the player is on fire.", false);
    private BooleanSetting noPortalOverlay = new BooleanSetting("norender_portal", "Portal Overlay", "Does not render the overlay when travelling through a portal.", false);
    private BooleanSetting noCrosshair = new BooleanSetting("norender_crosshair", "Crosshair", "Does not render the crosshair.", false);

    public NoRender() {
        super(new KeybindSetting("key.norender", "NoRender Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("NoRender");
        this.setCategory(Category.of("Render"));
        this.setDescription("Stops certain things from rendering.");

        this.addSetting(noEatParticles);
        this.addSetting(noTotemAnimation);
        this.addSetting(noVignette);
        this.addSetting(noPumpkinOverlay);
        this.addSetting(noFireOverlay);
        this.addSetting(noPortalOverlay);
        this.addSetting(noCrosshair);
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

    public boolean getNoEatParticles() {
        return this.noEatParticles.getValue();
    }

    public boolean getNoTotemAnimation() {
        return this.noTotemAnimation.getValue();
    }

    public boolean getNoVignette() {
        return this.noVignette.getValue();
    }

    public boolean getNoPumpkinOverlay() {
        return this.noPumpkinOverlay.getValue();
    }

    public boolean getNoFireOverlay() {
        return this.noFireOverlay.getValue();
    }

    public boolean getNoPortalOverlay() {
        return this.noPumpkinOverlay.getValue();
    }

    public boolean getNoCrosshair() {
        return this.noCrosshair.getValue();
    }
}
