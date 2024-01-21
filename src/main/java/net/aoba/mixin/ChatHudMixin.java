package net.aoba.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.google.common.collect.Lists;
import net.aoba.Aoba;
import net.aoba.cmd.GlobalChat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

	@Shadow
	private final List<ChatHudLine.Visible> visibleMessages = Lists.newArrayList();
	@Shadow
	private MinecraftClient client;
	@Shadow
	private int scrolledLines;
	@Shadow
	private boolean hasUnreadNewMessages;
	@Shadow
	public abstract boolean isChatHidden();
	@Shadow
	public abstract int getVisibleLineCount();
	@Shadow
	public abstract boolean isChatFocused();
	@Shadow
	public abstract double getChatScale();
	@Shadow
	public abstract int getWidth();
	@Shadow
	public abstract double toChatLineX(double x);
	@Shadow
	public abstract double toChatLineY(double y);
	@Shadow
	public abstract int getMessageIndex(double chatLineX, double chatLineY);
	@Shadow
	public abstract int getLineHeight();
	@Shadow
	public abstract int getIndicatorX(ChatHudLine.Visible line);
	@Shadow
	private static double getMessageOpacityMultiplier(int age) { return 1.0; }
	@Shadow
	public abstract void drawIndicatorIcon(DrawContext context, int x, int y, MessageIndicator.Icon icon);
	
	@Inject(at = { @At("HEAD") }, method = {"render(Lnet/minecraft/client/gui/DrawContext;III)V" }, cancellable = true)
	public void onRender(DrawContext context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
		if(GlobalChat.chatType == GlobalChat.ChatType.Global) {
			AobaChatRender(context, currentTick, mouseX, mouseY);
			ci.cancel();
		}
	}
	
	public void AobaChatRender(DrawContext context, int currentTick, int mouseX, int mouseY) {
        int x;
        int w;
        int v;
        int u;
        int t;
        int i = this.getVisibleLineCount();
        int j = this.visibleMessages.size();
        if (j <= 0) {
            return;
        }
        boolean bl = this.isChatFocused();
        float f = (float)this.getChatScale();
        int k = MathHelper.ceil((float)this.getWidth() / f);
        int l = context.getScaledWindowHeight();
        context.getMatrices().push();
        context.getMatrices().scale(f, f, 1.0f);
        context.getMatrices().translate(4.0f, 0.0f, 0.0f);
        int m = MathHelper.floor((float)(l - 40) / f);
        int n = this.getMessageIndex(this.toChatLineX(mouseX), this.toChatLineY(mouseY));
        double d = this.client.options.getChatOpacity().getValue() * (double)0.9f + (double)0.1f;
        double e = this.client.options.getTextBackgroundOpacity().getValue();
        double g = this.client.options.getChatLineSpacing().getValue();
        int o = this.getLineHeight();
        int p = (int)Math.round(-8.0 * (g + 1.0) + 4.0 * g);
        int q = 0;
        for (int r = 0; r < Aoba.getInstance().globalChat.messages.size() && r < i; ++r) {
            ChatHudLine.Visible visible = Aoba.getInstance().globalChat.messages.get(r);
            if (visible == null || (t = currentTick - visible.addedTime()) >= 200 && !bl) continue;
            double h = bl ? 1.0 : getMessageOpacityMultiplier(t);
            u = (int)(255.0 * h * d);
            v = (int)(255.0 * h * e);
            ++q;
            if (u <= 3) continue;
            w = 0;
            x = m - r * o;
            int y = x + p;
            context.getMatrices().push();
            context.getMatrices().translate(0.0f, 0.0f, 50.0f);
            context.fill(-4, x - o, 0 + k + 4 + 4, x, v << 24);
            MessageIndicator messageIndicator = visible.indicator();
            if (messageIndicator != null) {
                int z = messageIndicator.indicatorColor() | u << 24;
                context.fill(-4, x - o, -2, x, z);
                if (r == n && messageIndicator.icon() != null) {
                    int aa = this.getIndicatorX(visible);
                    int ab = y + this.client.textRenderer.fontHeight;
                    this.drawIndicatorIcon(context, aa, ab, messageIndicator.icon());
                }
            }
            context.getMatrices().translate(0.0f, 0.0f, 50.0f);
            context.drawTextWithShadow(this.client.textRenderer, visible.content(), 0, y, 0xFFFFFF + (u << 24));
            context.getMatrices().pop();
        }
        long ac = this.client.getMessageHandler().getUnprocessedMessageCount();
        if (ac > 0L) {
            int ad = (int)(128.0 * d);
            t = (int)(255.0 * e);
            context.getMatrices().push();
            context.getMatrices().translate(0.0f, m, 50.0f);
            context.fill(-2, 0, k + 4, 9, t << 24);
            context.getMatrices().translate(0.0f, 0.0f, 50.0f);
            context.drawTextWithShadow(this.client.textRenderer, Text.translatable("chat.queue", ac), 0, 1, 0xFFFFFF + (ad << 24));
            context.getMatrices().pop();
        }
        if (bl) {
            int ad = this.getLineHeight();
            t = j * ad;
            int ae = q * ad;
            int af = this.scrolledLines * ae / j - m;
            u = ae * ae / t;
            if (t != ae) {
                v = af > 0 ? 170 : 96;
                w = this.hasUnreadNewMessages ? 0xCC3333 : 0x3333AA;
                x = k + 4;
                context.fill(x, -af, x + 2, -af - u, 100, w + (v << 24));
                context.fill(x + 2, -af, x + 1, -af - u, 100, 0xCCCCCC + (v << 24));
            }
        }
        context.getMatrices().pop();
    }
}
