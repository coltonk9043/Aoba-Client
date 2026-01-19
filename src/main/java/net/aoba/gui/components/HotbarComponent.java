package net.aoba.gui.components;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Size;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.HotbarSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.util.List;

public class HotbarComponent extends Component {
    private final String text;
    private final HotbarSetting hotbar;
    private Runnable onClick;

    public HotbarComponent(HotbarSetting hotbar) {
        text = hotbar.displayName;
        this.hotbar = hotbar;

        setMargin(new Margin(8f, 2f, 8f, 2f));
    }

    @Override
    public void measure(Size availableSize) {
        preferredSize = new Size(availableSize.getWidth(), 60.0f);
    }

    /**
     * Draws multiple checkboxes to the screen representing the hotbar.
     *
     * @param drawContext  The current draw context of the game.
     * @param partialTicks The partial ticks used for interpolation.
     */
    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);

        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        float actualX = getActualSize().getX();
        float actualY = getActualSize().getY();

        float boxSize = 20;
        float spacing = 4;
        float startY = actualY + 8 + 18 + 4;

        // Determine fill color based on hotbar state
        List<Color> fillColors = hotbar.getValue().stream()
                .map(b -> b ? new Color(0, 154, 0, 200) : new Color(154, 0, 0, 200))
                .toList();

        Render2D.drawString(drawContext, text, actualX, actualY + 8, 0xFFFFFF);

        for (int i = 0; i < fillColors.size(); i++) {
            Color value = fillColors.get(i);
            Render2D.drawOutlinedRoundedBox(
                    drawContext,
                    actualX + i * (boxSize + spacing),
                    startY,
                    boxSize,
                    boxSize,
                    3,
                    GuiManager.borderColor.getValue(),
                    value
            );
        }
    }

    /**
     * Handles updating the Checkbox component.
     */
    @Override
    public void update() {
        super.update();
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
        super.onMouseClick(event);
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            if (hovered) {
                float actualX = getActualSize().getX();
                float actualY = getActualSize().getY();

                float boxSize = 20;
                float spacing = 4;
                float boxesStartY = actualY + 8 + 18 + 4;

                if (event.mouseY >= boxesStartY && event.mouseY <= boxesStartY + boxSize) {
                    for (int i = 0; i < hotbar.getValue().size(); i++) {
                        float boxStartX = actualX + i * (boxSize + spacing);
                        float boxEndX = boxStartX + boxSize;

                        if (event.mouseX >= boxStartX && event.mouseX <= boxEndX) {
                            hotbar.setValueAt(i, !hotbar.getValueAt(i));
                            if (onClick != null)
                                onClick.run();
                            event.cancel();
                            break;
                        }
                    }
                }
            }
        }
    }
}
