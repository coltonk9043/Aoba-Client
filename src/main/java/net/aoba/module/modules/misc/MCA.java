package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.KeybindSetting;
import net.aoba.utils.FindItemResult;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.invoke.util.InvokeUtil;

public class MCA extends Module implements MouseClickListener {
    private final EnumSetting<Mode> mode = new EnumSetting<>("mca_mode", "Mode", "The mode for the action to run when the middle mouse button is clicked.", Mode.FRIEND);

    private int selectedSlot;

    public MCA() {
        super(new KeybindSetting("key.mca", "MCA Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("MCA");
        this.setCategory(Category.of("misc"));
        this.setDescription("Middle Click Action");

        this.addSetting(mode);
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void OnMouseClick(MouseClickEvent mouseClickEvent) {
        if (mouseClickEvent.button == MouseButton.MIDDLE && mouseClickEvent.action == MouseAction.DOWN) {
            if (mode.getValue() == Mode.FRIEND) {
                if (MC.targetedEntity == null) return;
                if (!(MC.targetedEntity instanceof PlayerEntity player)) return;

                if (!Aoba.getInstance().friendsList.contains(player)) {
                    Aoba.getInstance().friendsList.addFriend(player);
                    mouseClickEvent.cancel();

                    sendChatMessage("Added " + player.getName().getString() + " to friends list.");
                } else {
                    Aoba.getInstance().friendsList.removeFriend(player);
                    mouseClickEvent.cancel();

                    sendChatMessage("Removed " + player.getName().getString() + " from friends list.");
                }
            } else if (mode.getValue() == Mode.PEARL) {
                FindItemResult result = find(Items.ENDER_PEARL);

                if (!result.found() || !result.isHotbar()) return;

                selectedSlot = MC.player.getInventory().selectedSlot;

                if (!result.isMainHand()) {
                    swap(result.slot(), false);

                    MC.interactionManager.interactItem(MC.player, Hand.MAIN_HAND);
                }
            }
        }
    }

    public enum Mode {
        FRIEND, PEARL
    }
}
