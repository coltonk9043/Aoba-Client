package aoba.main.gui.screens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import aoba.main.altmanager.Alt;
import aoba.main.altmanager.exceptions.InvalidResponseException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.optifine.http.HttpUtils;

public class AltSelectionList extends ExtendedList<AltSelectionList.Entry>
{
    private static final Logger LOGGER = LogManager.getLogger();

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
            NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.select", ((AltSelectionList.NormalEntry)this.getSelected()).alt.getEmail())).getString());
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
        private ResourceLocation icon;
        private long lastClickTime;

        protected NormalEntry(AltScreen p_i50669_2_, Alt p_i50669_3_)
        {
            this.owner = p_i50669_2_;
            this.alt = p_i50669_3_;
            this.mc = Minecraft.getInstance();
            this.icon = this.getPlayerHead(p_i50669_3_);
        }
        
        public ResourceLocation getPlayerHead(Alt alt) {
        	BufferedReader reader = null;
            try {
            	URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + alt.getUsername());
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuffer buffer = new StringBuffer();
                int read;
                char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1)
                    buffer.append(chars, 0, read); 

               String str = buffer.toString();
               JsonObject responseObject = new JsonParser().parse(str).getAsJsonObject();
               System.out.println(alt.getUsername() + ", " + responseObject.get("id").getAsString());
               return new ResourceLocation("minecraft:skins/" + responseObject.get("id").getAsString());
            } catch(Exception e) {
            	e.printStackTrace();
            	return AbstractClientPlayerEntity.getLocationSkin(alt.getUsername());
            }
        }
        
        public void render(MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            this.mc.fontRenderer.drawString(matrixStack, "Username: " + this.alt.getEmail(), (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 2), 16777215);
            String s = "";
            for(char i : this.alt.getPassword().toCharArray()) {
            	s = s + "*";
            }
            this.mc.fontRenderer.drawString(matrixStack, "Password: " + s, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12), 16777215);
            this.mc.fontRenderer.drawString(matrixStack, this.alt.isCracked() ? "Cracked Account" : "Premium Account", (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 22), this.alt.isCracked() ? 0xFF0000 : 0x00FF00);

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawHeads(matrixStack, p_230432_4_ + 4, p_230432_3_+ 4);
        }

        public void getAltList()
        {
            this.owner.getAltList();
        }

        public void drawHeads(MatrixStack matrixStack, int p_238859_2_, int p_238859_3_) {
        	this.mc.getTextureManager().bindTexture(this.icon);
            RenderSystem.enableBlend();
            // Face
            AbstractGui.blit(matrixStack, p_238859_2_, p_238859_3_, 24, 24, 24, 24, 192, 192);
            AbstractGui.blit(matrixStack, p_238859_2_, p_238859_3_, 120, 24, 24, 24, 192, 192);
            
            RenderSystem.disableBlend();
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            if (Screen.hasShiftDown())
            {
                AltSelectionList AltSelectionList = this.owner.altListSelector;
                int i = AltSelectionList.getEventListeners().indexOf(this);
                if (keyCode == 264 && i < this.owner.getAltList().size() - 1 || keyCode == 265 && i > 0)
                {
                    return true;
                }
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
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
                    return true;
                }
                if (d0 < 16.0D && d1 > 16.0D && i < this.owner.getAltList().size() - 1)
                {
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