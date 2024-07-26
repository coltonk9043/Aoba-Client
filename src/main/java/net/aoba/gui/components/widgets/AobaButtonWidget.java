package net.aoba.gui.components.widgets;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.aoba.gui.GuiManager;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class AobaButtonWidget extends ClickableWidget {
    public AobaButtonWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    	Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
    	
    	RenderSystem.disableCull();
    	
    	RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Render2D.drawRoundedBox(matrix, getX(), getY(), width, height, GuiManager.roundingRadius.getValue(), Color.convertHextoRGB("FF000000"));
		Render2D.drawRoundedOutline(matrix, getX(), getY(), width, height, GuiManager.roundingRadius.getValue(), Color.convertHextoRGB("FFFFFF"));
		RenderSystem.enableCull();
    }
    
    @Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		// For brevity, we'll just skip this for now - if you want to add narration to your widget, you can do so here.
		return;
	}
}