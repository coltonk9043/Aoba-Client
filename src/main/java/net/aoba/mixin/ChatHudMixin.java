/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.hud.ChatHud;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

	/*
	 * @Shadow private final List<ChatHudLine.Visible> visibleMessages =
	 * Lists.newArrayList();
	 * 
	 * @Shadow private MinecraftClient client;
	 * 
	 * @Shadow private int scrolledLines;
	 * 
	 * @Shadow private boolean hasUnreadNewMessages;
	 * 
	 * @Shadow public abstract boolean isChatHidden();
	 * 
	 * @Shadow public abstract int getVisibleLineCount();
	 * 
	 * @Shadow public abstract boolean isChatFocused();
	 * 
	 * @Shadow public abstract double getChatScale();
	 * 
	 * @Shadow public abstract int getWidth();
	 * 
	 * @Shadow public abstract double toChatLineX(double x);
	 * 
	 * @Shadow public abstract double toChatLineY(double y);
	 * 
	 * @Shadow public abstract int getMessageIndex(double chatLineX, double
	 * chatLineY);
	 * 
	 * @Shadow public abstract int getLineHeight();
	 * 
	 * @Shadow public abstract int getIndicatorX(ChatHudLine.Visible line);
	 * 
	 * @Shadow private static double getMessageOpacityMultiplier(int age) { return
	 * 1.0; }
	 * 
	 * @Shadow public abstract void drawIndicatorIcon(DrawContext context, int x,
	 * int y, MessageIndicator.Icon icon);
	 * 
	 * @Inject(at = { @At("HEAD") }, method = {
	 * "render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V" }, cancellable = true)
	 * public void onRender(DrawContext context, int currentTick, int mouseX, int
	 * mouseY, boolean focused, CallbackInfo ci) { if (GlobalChat.chatType ==
	 * GlobalChat.ChatType.Global) { AobaChatRender(context, currentTick, mouseX,
	 * mouseY); ci.cancel(); } }
	 * 
	 * public void AobaChatRender(DrawContext context, int currentTick, int mouseX,
	 * int mouseY) { int x; int v; int u; int t; int visibleLinesCount =
	 * getVisibleLineCount();
	 * 
	 * Matrix3x2fStack matrixStack = context.getMatrices();
	 * 
	 * boolean bl = isChatFocused(); float f = (float) getChatScale(); int k =
	 * MathHelper.ceil((float) getWidth() / f); int l =
	 * context.getScaledWindowHeight(); matrixStack.pushMatrix();
	 * matrixStack.scale(f, f); matrixStack.translate(4.0f, 0.0f, 0.0f); int m =
	 * MathHelper.floor((float) (l - 40) / f); int n =
	 * getMessageIndex(toChatLineX(mouseX), toChatLineY(mouseY)); double opacity =
	 * client.options.getChatOpacity().getValue() * (double) 0.9f + (double) 0.1f;
	 * double textOpacity = client.options.getTextBackgroundOpacity().getValue();
	 * double chatSpacing = client.options.getChatLineSpacing().getValue(); int
	 * lineHeight = getLineHeight(); int p = (int) Math.round(-8.0 * (chatSpacing +
	 * 1.0) + 4.0 * chatSpacing);
	 * 
	 * for (int r = 0; r < Aoba.getInstance().globalChat.messages.size() && r <
	 * visibleLinesCount; ++r) { ChatHudLine.Visible visible =
	 * Aoba.getInstance().globalChat.messages.get(r); if (visible == null || (t =
	 * currentTick - visible.addedTime()) >= 200 && !bl) continue; double h = bl ?
	 * 1.0 : getMessageOpacityMultiplier(t); u = (int) (255.0 * h * opacity); v =
	 * (int) (255.0 * h * textOpacity); if (u <= 3) continue; x = m - r *
	 * lineHeight; int y = x + p; matrixStack.pushMatrix();
	 * matrixStack.translate(0.0f, 0.0f); context.fill(-4, x - lineHeight, k + 4 +
	 * 4, x, v << 24); MessageIndicator messageIndicator = visible.indicator(); if
	 * (messageIndicator != null) { int z = messageIndicator.indicatorColor() | u <<
	 * 24; context.fill(-4, x - lineHeight, -2, x, z); if (r == n &&
	 * messageIndicator.icon() != null) { int aa = getIndicatorX(visible); int ab =
	 * y + client.textRenderer.fontHeight; drawIndicatorIcon(context, aa, ab,
	 * messageIndicator.icon()); } } matrixStack.translate(0.0f, 0.0f, 50.0f);
	 * context.drawTextWithShadow(client.textRenderer, visible.content(), 0, y,
	 * 0xFFFFFF + (u << 24)); matrixStack.popMatrix(); }
	 * 
	 * matrixStack.popMatrix(); }
	 */
}
