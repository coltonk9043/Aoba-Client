package aoba.main.gui;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;

public class ArmorHUD extends Tab{

	private ItemRenderer itemRenderer; 
	public ArmorHUD() {
		itemRenderer = mc.getItemRenderer();
		this.width = 16;
		this.x = 300;
		this.y = 300;
		this.height = 64;
	}
	
	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		if (mc.aoba.hm.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x *  mc.gameSettings.guiScale) && mouseX <= (x + width) *  mc.gameSettings.guiScale) {
					if (mouseY >= (y *  mc.gameSettings.guiScale) && mouseY <= (y + height) *  mc.gameSettings.guiScale) {
						if (mouseClicked) {
							HudManager.currentGrabbed = this;
						}
					}
				}
			}
		}
	}

	@Override
	public void draw(int scaledWidth, int scaledHeight, Color color) {
		NonNullList<ItemStack> armors = mc.player.inventory.armorInventory;
		int yOff = 16;
		System.out.println(armors.get(0).getDisplayName().getString());
		for(ItemStack armor : armors) {
			if(armor.getItem() == Items.AIR) continue;
			drawItemStack(armor, this.x, this.y + this.height - yOff);
			yOff += 16;
		}
	}

    private void drawItemStack(ItemStack stack, int x, int y)
    {
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, null);
    }
}
