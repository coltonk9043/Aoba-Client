package net.aoba.gui;

import net.aoba.Aoba;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class ArmorHUD extends Tab{

	private ItemRenderer itemRenderer; 
	
	public ArmorHUD() {
		itemRenderer = mc.getItemRenderer();
		this.x = 300;
		this.y = 300;
		this.width = 64;
		this.height = 256;
	}
	
	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		if (Aoba.getInstance().hm.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x) && mouseX <= (x + width)) {
					if (mouseY >= (y) && mouseY <= (y + height)) {
						if (mouseClicked) {
							HudManager.currentGrabbed = this;
						}
					}
				}
			}
		}
	}

	@Override
	public void draw(MatrixStack matrixStack, float partialTicks, Color color) {
		DefaultedList<ItemStack> armors = mc.player.getInventory().armor;
		int yOff = 16;
		System.out.println(armors.get(0).getName().getString());
		for(ItemStack armor : armors) {
			if(armor.getItem() == Items.AIR) continue;
			drawItemStack(armor, this.x, this.y + this.height - yOff);
			yOff += 16;
		}
	}

    private void drawItemStack(ItemStack stack, int x, int y)
    {
    	this.itemRenderer.renderGuiItemIcon(stack, x, y);
    	this.itemRenderer.renderGuiItemOverlay(mc.textRenderer, stack, x, y);
    }
}
