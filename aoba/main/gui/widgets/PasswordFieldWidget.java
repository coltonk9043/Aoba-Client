package aoba.main.gui.widgets;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class PasswordFieldWidget extends Widget implements IRenderable, IGuiEventListener
{
    private final FontRenderer fontRenderer;
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    private boolean isEnabled = true;
    private boolean field_212956_h;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;
    private String suggestion;
    private Consumer<String> guiResponder;
    private Predicate<String> validator = Objects::nonNull;
    private BiFunction<String, Integer, IReorderingProcessor> textFormatter = (p_195610_0_, p_195610_1_) ->
    {
        return IReorderingProcessor.func_242239_a(p_195610_0_, Style.EMPTY);
    };

    public PasswordFieldWidget(FontRenderer p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, ITextComponent p_i232260_6_)
    {
        this(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, (TextFieldWidget)null, p_i232260_6_);
    }

    public PasswordFieldWidget(FontRenderer p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable TextFieldWidget p_i232259_6_, ITextComponent p_i232259_7_)
    {
        super(p_i232259_2_, p_i232259_3_, p_i232259_4_, p_i232259_5_, p_i232259_7_);
        this.fontRenderer = p_i232259_1_;

        if (p_i232259_6_ != null)
        {
            this.setText(p_i232259_6_.getText());
        }
    }

    public void setResponder(Consumer<String> rssponderIn)
    {
        this.guiResponder = rssponderIn;
    }

    public void setTextFormatter(BiFunction<String, Integer, IReorderingProcessor> textFormatterIn)
    {
        this.textFormatter = textFormatterIn;
    }

    public void tick()
    {
        ++this.cursorCounter;
    }

    protected IFormattableTextComponent getNarrationMessage()
    {
        ITextComponent itextcomponent = this.getMessage();
        return new TranslationTextComponent("gui.narrate.editBox", itextcomponent, this.text);
    }

    public void setText(String textIn)
    {
        if (this.validator.test(textIn))
        {
            if (textIn.length() > this.maxStringLength)
            {
                this.text = textIn.substring(0, this.maxStringLength);
            }
            else
            {
                this.text = textIn;
            }

            this.setCursorPositionEnd();
            this.setSelectionPos(this.cursorPosition);
            this.onTextChanged(textIn);
        }
    }

    public String getText()
    {
        return this.text;
    }

    public String getSelectedText()
    {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }

    public void setValidator(Predicate<String> validatorIn)
    {
        this.validator = validatorIn;
    }

    public void writeText(String textToWrite)
    {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int k = this.maxStringLength - this.text.length() - (i - j);
        String s = SharedConstants.filterAllowedCharacters(textToWrite);
        int l = s.length();

        if (k < l)
        {
            s = s.substring(0, k);
            l = k;
        }

        String s1 = (new StringBuilder(this.text)).replace(i, j, s).toString();

        if (this.validator.test(s1))
        {
            this.text = s1;
            this.clampCursorPosition(i + l);
            this.setSelectionPos(this.cursorPosition);
            this.onTextChanged(this.text);
        }
    }

    private void onTextChanged(String newText)
    {
        if (this.guiResponder != null)
        {
            this.guiResponder.accept(newText);
        }

        this.nextNarration = Util.milliTime() + 500L;
    }

    private void delete(int p_212950_1_)
    {
        if (Screen.hasControlDown())
        {
            this.deleteWords(p_212950_1_);
        }
        else
        {
            this.deleteFromCursor(p_212950_1_);
        }
    }

    public void deleteWords(int num)
    {
        if (!this.text.isEmpty())
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int num)
    {
        if (!this.text.isEmpty())
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                int i = this.func_238516_r_(num);
                int j = Math.min(i, this.cursorPosition);
                int k = Math.max(i, this.cursorPosition);

                if (j != k)
                {
                    String s = (new StringBuilder(this.text)).delete(j, k).toString();

                    if (this.validator.test(s))
                    {
                        this.text = s;
                        this.setCursorPosition(j);
                    }
                }
            }
        }
    }

    public int getNthWordFromCursor(int numWords)
    {
        return this.getNthWordFromPos(numWords, this.getCursorPosition());
    }

    private int getNthWordFromPos(int n, int pos)
    {
        return this.getNthWordFromPosWS(n, pos, true);
    }

    private int getNthWordFromPosWS(int n, int pos, boolean skipWs)
    {
        int i = pos;
        boolean flag = n < 0;
        int j = Math.abs(n);

        for (int k = 0; k < j; ++k)
        {
            if (!flag)
            {
                int l = this.text.length();
                i = this.text.indexOf(32, i);

                if (i == -1)
                {
                    i = l;
                }
                else
                {
                    while (skipWs && i < l && this.text.charAt(i) == ' ')
                    {
                        ++i;
                    }
                }
            }
            else
            {
                while (skipWs && i > 0 && this.text.charAt(i - 1) == ' ')
                {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != ' ')
                {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursorBy(int num)
    {
        this.setCursorPosition(this.func_238516_r_(num));
    }

    private int func_238516_r_(int p_238516_1_)
    {
        return Util.func_240980_a_(this.text, this.cursorPosition, p_238516_1_);
    }

    public void setCursorPosition(int pos)
    {
        this.clampCursorPosition(pos);

        if (!this.field_212956_h)
        {
            this.setSelectionPos(this.cursorPosition);
        }

        this.onTextChanged(this.text);
    }

    public void clampCursorPosition(int pos)
    {
        this.cursorPosition = MathHelper.clamp(pos, 0, this.text.length());
    }

    public void setCursorPositionZero()
    {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd()
    {
        this.setCursorPosition(this.text.length());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (!this.canWrite())
        {
            return false;
        }
        else
        {
            this.field_212956_h = Screen.hasShiftDown();

            if (Screen.isSelectAll(keyCode))
            {
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return true;
            }
            else if (Screen.isCopy(keyCode))
            {
                Minecraft.getInstance().keyboardListener.setClipboardString(this.getSelectedText());
                return true;
            }
            else if (Screen.isPaste(keyCode))
            {
                if (this.isEnabled)
                {
                    this.writeText(Minecraft.getInstance().keyboardListener.getClipboardString());
                }

                return true;
            }
            else if (Screen.isCut(keyCode))
            {
                Minecraft.getInstance().keyboardListener.setClipboardString(this.getSelectedText());

                if (this.isEnabled)
                {
                    this.writeText("");
                }

                return true;
            }
            else
            {
                switch (keyCode)
                {
                    case 259:
                        if (this.isEnabled)
                        {
                            this.field_212956_h = false;
                            this.delete(-1);
                            this.field_212956_h = Screen.hasShiftDown();
                        }

                        return true;

                    case 260:
                    case 264:
                    case 265:
                    case 266:
                    case 267:
                    default:
                        return false;

                    case 261:
                        if (this.isEnabled)
                        {
                            this.field_212956_h = false;
                            this.delete(1);
                            this.field_212956_h = Screen.hasShiftDown();
                        }

                        return true;

                    case 262:
                        if (Screen.hasControlDown())
                        {
                            this.setCursorPosition(this.getNthWordFromCursor(1));
                        }
                        else
                        {
                            this.moveCursorBy(1);
                        }

                        return true;

                    case 263:
                        if (Screen.hasControlDown())
                        {
                            this.setCursorPosition(this.getNthWordFromCursor(-1));
                        }
                        else
                        {
                            this.moveCursorBy(-1);
                        }

                        return true;

                    case 268:
                        this.setCursorPositionZero();
                        return true;

                    case 269:
                        this.setCursorPositionEnd();
                        return true;
                }
            }
        }
    }

    public boolean canWrite()
    {
        return this.getVisible() && this.isFocused() && this.isEnabled();
    }

    public boolean charTyped(char codePoint, int modifiers)
    {
        if (!this.canWrite())
        {
            return false;
        }
        else if (SharedConstants.isAllowedCharacter(codePoint))
        {
            if (this.isEnabled)
            {
                this.writeText(Character.toString(codePoint));
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (!this.getVisible())
        {
            return false;
        }
        else
        {
            boolean flag = mouseX >= (double)this.x && mouseX < (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY < (double)(this.y + this.height);

            if (this.canLoseFocus)
            {
                this.setFocused2(flag);
            }

            if (this.isFocused() && flag && button == 0)
            {
                int i = MathHelper.floor(mouseX) - this.x;

                if (this.enableBackgroundDrawing)
                {
                    i -= 4;
                }

                String s = this.fontRenderer.func_238412_a_(this.text.substring(this.lineScrollOffset), this.getAdjustedWidth());
                this.setCursorPosition(this.fontRenderer.func_238412_a_(s, i).length() + this.lineScrollOffset);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public void setFocused2(boolean isFocusedIn)
    {
        super.setFocused(isFocusedIn);
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.getVisible())
        {
            if (this.getEnableBackgroundDrawing())
            {
                int i = this.isFocused() ? -1 : -6250336;
                fill(matrixStack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, i);
                fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
            }

            int i2 = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.fontRenderer.func_238412_a_(this.text.substring(this.lineScrollOffset), this.getAdjustedWidth()).replaceAll(".", "*");
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused() && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? this.x + 4 : this.x;
            int i1 = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
            int j1 = l;

            if (k > s.length())
            {
                k = s.length();
            }

            if (!s.isEmpty())
            {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.fontRenderer.func_238407_a_(matrixStack, this.textFormatter.apply(s1, this.lineScrollOffset), (float)l, (float)i1, i2);
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag)
            {
                k1 = j > 0 ? l + this.width : l;
            }
            else if (flag2)
            {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length())
            {
                this.fontRenderer.func_238407_a_(matrixStack, this.textFormatter.apply(s.substring(j), this.cursorPosition), (float)j1, (float)i1, i2);
            }

            if (!flag2 && this.suggestion != null)
            {
                this.fontRenderer.drawStringWithShadow(matrixStack, this.suggestion, (float)(k1 - 1), (float)i1, -8355712);
            }

            if (flag1)
            {
                if (flag2)
                {
                    AbstractGui.fill(matrixStack, k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
                }
                else
                {
                    this.fontRenderer.drawStringWithShadow(matrixStack, "_", (float)k1, (float)i1, i2);
                }
            }

            if (k != j)
            {
                int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
                this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
            }
        }
    }

    private void drawSelectionBox(int startX, int startY, int endX, int endY)
    {
        if (startX < endX)
        {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY)
        {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.x + this.width)
        {
            endX = this.x + this.width;
        }

        if (startX > this.x + this.width)
        {
            startX = this.x + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)startX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)startY, 0.0D).endVertex();
        bufferbuilder.pos((double)startX, (double)startY, 0.0D).endVertex();
        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    public void setMaxStringLength(int length)
    {
        this.maxStringLength = length;

        if (this.text.length() > length)
        {
            this.text = this.text.substring(0, length);
            this.onTextChanged(this.text);
        }
    }

    private int getMaxStringLength()
    {
        return this.maxStringLength;
    }

    public int getCursorPosition()
    {
        return this.cursorPosition;
    }

    private boolean getEnableBackgroundDrawing()
    {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn)
    {
        this.enableBackgroundDrawing = enableBackgroundDrawingIn;
    }

    public void setTextColor(int color)
    {
        this.enabledColor = color;
    }

    public void setDisabledTextColour(int color)
    {
        this.disabledColor = color;
    }

    public boolean changeFocus(boolean focus)
    {
        return this.visible && this.isEnabled ? super.changeFocus(focus) : false;
    }

    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return this.visible && mouseX >= (double)this.x && mouseX < (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY < (double)(this.y + this.height);
    }

    protected void onFocusedChanged(boolean focused)
    {
        if (focused)
        {
            this.cursorCounter = 0;
        }
    }

    private boolean isEnabled()
    {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.isEnabled = enabled;
    }

    public int getAdjustedWidth()
    {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public void setSelectionPos(int position)
    {
        int i = this.text.length();
        this.selectionEnd = MathHelper.clamp(position, 0, i);

        if (this.fontRenderer != null)
        {
            if (this.lineScrollOffset > i)
            {
                this.lineScrollOffset = i;
            }

            int j = this.getAdjustedWidth();
            String s = this.fontRenderer.func_238412_a_(this.text.substring(this.lineScrollOffset), j);
            int k = s.length() + this.lineScrollOffset;

            if (this.selectionEnd == this.lineScrollOffset)
            {
                this.lineScrollOffset -= this.fontRenderer.func_238413_a_(this.text, j, true).length();
            }

            if (this.selectionEnd > k)
            {
                this.lineScrollOffset += this.selectionEnd - k;
            }
            else if (this.selectionEnd <= this.lineScrollOffset)
            {
                this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;
            }

            this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
        }
    }

    public void setCanLoseFocus(boolean canLoseFocusIn)
    {
        this.canLoseFocus = canLoseFocusIn;
    }

    public boolean getVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean isVisible)
    {
        this.visible = isVisible;
    }

    public void setSuggestion(@Nullable String p_195612_1_)
    {
        this.suggestion = p_195612_1_;
    }

    public int func_195611_j(int p_195611_1_)
    {
        return p_195611_1_ > this.text.length() ? this.x : this.x + this.fontRenderer.getStringWidth(this.text.substring(0, p_195611_1_));
    }

    public void setX(int xIn)
    {
        this.x = xIn;
    }
}
