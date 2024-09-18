package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.SendMovementPacketEvent;
import net.aoba.event.events.SendPacketEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.SendMovementPacketListener;
import net.aoba.event.listeners.SendPacketListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.mixin.interfaces.IPlayerMoveC2SPacket;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;

public class AntiHunger extends Module implements SendPacketListener, SendMovementPacketListener {
    private boolean lastOnGround, ignorePacket;

    public BooleanSetting sprint = new BooleanSetting("antihunger_sprint", "Sprint", "Change sprint packets.", true);
    public BooleanSetting onGround = new BooleanSetting("antihunger_onground", "On Ground", "Fakes onGround.", true);

    public AntiHunger() {
        super(new KeybindSetting("key.antihunger", "AntiHunger Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("AntiHunger");
        this.setCategory(Category.of("misc"));
        this.setDescription("Reduces the amount of hunger that is consumed.");

        this.addSetting(sprint);
        this.addSetting(onGround);
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(SendPacketListener.class, this);
        Aoba.getInstance().eventManager.RemoveListener(SendMovementPacketListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(SendPacketListener.class, this);
        Aoba.getInstance().eventManager.AddListener(SendMovementPacketListener.class, this);

        lastOnGround = MC.player.isOnGround();
    }

    @Override
    public void onToggle() {

    }


    @Override
    public void OnSendPacket(SendPacketEvent event) {
        if (ignorePacket && event.GetPacket() instanceof PlayerMoveC2SPacket) {
            ignorePacket = false;
            return;
        }

        if (MC.player.hasVehicle() || MC.player.isTouchingWater() || MC.player.isSubmergedInWater()) return;

        if (event.GetPacket() instanceof ClientCommandC2SPacket packet && sprint.getValue()) {
            if (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) event.cancel();
        }

        if (event.GetPacket() instanceof PlayerMoveC2SPacket packet && onGround.getValue() && MC.player.isOnGround() && MC.player.fallDistance <= 0.0 && !MC.interactionManager.isBreakingBlock()) {
            ((IPlayerMoveC2SPacket) packet).setOnGround(false);
        }
    }

    @Override
    public void onSendMovementPacket(SendMovementPacketEvent.Pre event) {
        if (MC.player.isOnGround() && !lastOnGround && onGround.getValue()) {
            ignorePacket = true;
        }

        lastOnGround = MC.player.isOnGround();
    }

    @Override
    public void onSendMovementPacket(SendMovementPacketEvent.Post event) {

    }
}
