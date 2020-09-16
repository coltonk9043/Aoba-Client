package aoba.main.gui.screens;

import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import aoba.main.altmanager.Alt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class AltSelectionList extends ExtendedList<AltSelectionList.Entry>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation field_214359_c = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation field_214360_d = new ResourceLocation("textures/gui/server_selection.png");
    private final AltScreen owner;
    private final List<AltSelectionList.NormalEntry> altList = Lists.newArrayList();

    public AltSelectionList(AltScreen ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    public void updateAlts()
    {
    	this.clearEntries();
        for(Alt alt : this.owner.getAltList())
        {
        	AltSelectionList.NormalEntry entry = new AltSelectionList.NormalEntry(this.owner, alt);
        	altList.add(entry);
        }
        this.setList();
    }
    
    private void setList()
    {
        this.altList.forEach(this::addEntry);
    }

    public void setSelected(@Nullable AltSelectionList.Entry entry)
    {
        super.setSelected(entry);

        if (this.getSelected() instanceof AltSelectionList.NormalEntry)
        {
            NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.select", ((AltSelectionList.NormalEntry)this.getSelected()).alt.getUsername())).getString());
        }

        this.owner.func_214295_b();
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        AltSelectionList.Entry AltSelectionList$entry = this.getSelected();
        return AltSelectionList$entry != null && AltSelectionList$entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void moveSelection(AbstractList.Ordering p_241219_1_)
    {
        this.func_241572_a_(p_241219_1_, (p_241612_0_) ->
        {
            return !(p_241612_0_ instanceof AltSelectionList.NormalEntry);
        });
    }

    protected int getScrollbarPosition()
    {
        return super.getScrollbarPosition() + 30;
    }

    public int getRowWidth()
    {
        return super.getRowWidth() + 85;
    }

    protected boolean isFocused()
    {
        return this.owner.getListener() == this;
    }

    public abstract static class Entry extends ExtendedList.AbstractListEntry<AltSelectionList.Entry>
    {
    }

    public class NormalEntry extends AltSelectionList.Entry
    {
        private final AltScreen owner;
        private final Minecraft mc;
        private final Alt alt;
        private final ResourceLocation altIcon;
        private String lastIconB64;
        private DynamicTexture icon;
        private long lastClickTime;

        protected NormalEntry(AltScreen p_i50669_2_, Alt p_i50669_3_)
        {
            this.owner = p_i50669_2_;
            this. alt = p_i50669_3_;
            this.mc = Minecraft.getInstance();
            
            this.altIcon = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars(p_i50669_3_.getUsername()) + "/icon");
            this.icon = (DynamicTexture)this.mc.getTextureManager().getTexture(AbstractClientPlayerEntity.getLocationSkin(alt.getUsername()));
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            this.mc.fontRenderer.drawString(p_230432_1_, "Username: " + this.alt.getUsername(), (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 2), 16777215);
            String s = "";
            for(char i : this.alt.getPassword().toCharArray()) {
            	s = s + "*";
            }
            this.mc.fontRenderer.drawString(p_230432_1_, "Password: " + s, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12), 16777215);
            this.mc.fontRenderer.drawString(p_230432_1_, this.alt.isCracked() ? "Cracked Account" : "Premium Account", (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 22), this.alt.isCracked() ? 0xFF0000 : 0x00FF00);
            int k = 0;
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            this.mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
            AbstractGui.blit(p_230432_1_, p_230432_4_ + p_230432_5_ - 15, p_230432_3_, (float)(k * 10), (float)(176 * 8), 10, 8, 256, 256);
       
            if (this.icon != null)
            {
                this.func_238859_a_(p_230432_1_, p_230432_4_, p_230432_3_, this.altIcon);
            }
            else
            {
                this.func_238859_a_(p_230432_1_, p_230432_4_, p_230432_3_, AltSelectionList.field_214359_c);
            }

            if (this.mc.gameSettings.touchscreen || p_230432_9_)
            {
                this.mc.getTextureManager().bindTexture(AltSelectionList.field_214360_d);
                AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + 32, p_230432_3_ + 32, -1601138544);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                int k1 = p_230432_7_ - p_230432_4_;
                int l1 = p_230432_8_ - p_230432_3_;
                    if (k1 < 32 && k1 > 16)
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 32.0F, 32, 32, 256, 256);
                    }
                    else
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 0.0F, 32, 32, 256, 256);
                    }
                if (p_230432_2_ > 0)
                {
                    if (k1 < 16 && l1 < 16)
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 32.0F, 32, 32, 256, 256);
                    }
                    else
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 0.0F, 32, 32, 256, 256);
                    }
                }
                if (p_230432_2_ < this.owner.getAltList().size() - 1)
                {
                    if (k1 < 16 && l1 > 16)
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 32.0F, 32, 32, 256, 256);
                    }
                    else
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 0.0F, 32, 32, 256, 256);
                    }
                }
            }
        }

        public void func_241613_a_()
        {
            this.owner.getAltList();
        }

        protected void func_238859_a_(MatrixStack p_238859_1_, int p_238859_2_, int p_238859_3_, ResourceLocation p_238859_4_)
        {
            this.mc.getTextureManager().bindTexture(p_238859_4_);
            RenderSystem.enableBlend();
            AbstractGui.blit(p_238859_1_, p_238859_2_, p_238859_3_, 0.0F, 0.0F, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }

        private boolean func_241614_a_(@Nullable String p_241614_1_)
        {
            if (p_241614_1_ == null)
            {
                this.mc.getTextureManager().deleteTexture(this.altIcon);

                if (this.icon != null && this.icon.getTextureData() != null)
                {
                    this.icon.getTextureData().close();
                }

                this.icon = null;
            }
            else
            {
                try
                {
                    NativeImage nativeimage = NativeImage.readBase64(p_241614_1_);
                    Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
                    Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");

                    if (this.icon == null)
                    {
                        this.icon = new DynamicTexture(nativeimage);
                    }
                    else
                    {
                        this.icon.setTextureData(nativeimage);
                        this.icon.updateDynamicTexture();
                    }

                    this.mc.getTextureManager().loadTexture(this.altIcon, this.icon);
                }
                catch (Throwable throwable)
                {
                    AltSelectionList.LOGGER.error("Invalid icon for alt {} ({})", this.alt.getUsername(), throwable);
                    return false;
                }
            }

            return true;
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            if (Screen.hasShiftDown())
            {
                AltSelectionList AltSelectionList = this.owner.altListSelector;
                int i = AltSelectionList.getEventListeners().indexOf(this);

                if (keyCode == 264 && i < this.owner.getAltList().size() - 1 || keyCode == 265 && i > 0)
                {
                    this.func_228196_a_(i, keyCode == 264 ? i + 1 : i - 1);
                    return true;
                }
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        private void func_228196_a_(int p_228196_1_, int p_228196_2_)
        {

        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            double d0 = mouseX - (double)AltSelectionList.this.getRowLeft();
            double d1 = mouseY - (double)AltSelectionList.this.getRowTop(AltSelectionList.this.getEventListeners().indexOf(this));

            if (d0 <= 32.0D)
            {
                if (d0 < 32.0D && d0 > 16.0D)
                {
                    this.owner.func_214287_a(this);
                    this.owner.loginToSelected();
                    return true;
                }

                int i = this.owner.altListSelector.getEventListeners().indexOf(this);

                if (d0 < 16.0D && d1 < 16.0D && i > 0)
                {
                    this.func_228196_a_(i, i - 1);
                    return true;
                }

                if (d0 < 16.0D && d1 > 16.0D && i < this.owner.getAltList().size() - 1)
                {
                    this.func_228196_a_(i, i + 1);
                    return true;
                }
            }

            this.owner.func_214287_a(this);

            if (Util.milliTime() - this.lastClickTime < 250L)
            {
                this.owner.loginToSelected();
            }

            this.lastClickTime = Util.milliTime();
            return false;
        }

        public Alt getAltData()
        {
            return this.alt;
        }
    }
}