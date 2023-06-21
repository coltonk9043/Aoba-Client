package net.aoba.mixin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.interfaces.IChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.minecraft.text.OrderedText;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
	@Shadow
	private TextFieldWidget textField;
	@Shadow
	@Nullable
	private ParseResults<CommandSource> parse;
	@Shadow
	private CompletableFuture<Suggestions> pendingSuggestions;
	@Shadow
	private List<OrderedText> messages;

	@Shadow
	public abstract void show(boolean narrateFirstSuggestion);
	
	@Inject(at = {
			@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getCursor()I", ordinal = 0) }, method = "refresh()V", cancellable = true)
	private void onRefresh(CallbackInfo ci) {
		String prefix = AobaClient.PREFIX;
		String string = this.textField.getText();

		if(string.length() > 0) {
			SuggestionsBuilder builder;
			int cursorPos = this.textField.getCursor();
			
			String string2 = string.substring(0, cursorPos);
			
			int j = 0;
			Matcher matcher = Pattern.compile("(\\s+)").matcher(string2);
			while (matcher.find()) {
				j = matcher.end();
			}
			
			if(string.charAt(0) == '.') {
				builder = new SuggestionsBuilder(string2, j);
				if(string.length() <= prefix.length() && string.equals(prefix.substring(0, string.length()))) {
					builder.suggest(prefix + " ");
				}else {
					
					for (Object strObj : Aoba.getInstance().commandManager.getCommands().keySet().toArray()) {
						String str = (String) strObj;
						builder.suggest(str);
					}
				}
				this.pendingSuggestions = builder.buildFuture();
				this.show(false);
				ci.cancel();
			}
		}
	}
}
