package net.aoba.gui.components.widgets;

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
        Render2D.drawRoundedBox(context.getMatrices().peek().getPositionMatrix(), getX(), getY(), getX() + this.width, getY() + this.height, 6, Color.convertHextoRGB("FF000000"));
        Render2D.drawRoundedOutline(context.getMatrices().peek().getPositionMatrix(), getX(), getY(), this.width, this.height, 6, Color.convertHextoRGB("FFFFFFFF"));
    }
    
    @Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		// For brevity, we'll just skip this for now - if you want to add narration to your widget, you can do so here.
		return;
	}
}