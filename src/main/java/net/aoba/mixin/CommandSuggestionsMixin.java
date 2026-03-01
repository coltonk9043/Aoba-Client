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

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.aoba.Aoba;
import net.aoba.command.Command;
import net.aoba.managers.CommandManager;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {
    @Shadow
    private EditBox input;
    @Shadow
    @Nullable
    private ParseResults<SharedSuggestionProvider> currentParse;
    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;
    @Shadow
    private List<FormattedCharSequence> commandUsage;

    @Shadow
    public abstract void showSuggestions(boolean narrateFirstSuggestion);

    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;getCursorPosition()I", ordinal = 0)}, method = "updateCommandInfo()V", cancellable = true)
    private void onRefresh(CallbackInfo ci) {
        String prefix = CommandManager.PREFIX.getValue();
        String string = input.getValue();

        if (string.length() > 0) {
            int cursorPos = input.getCursorPosition();
            String string2 = string.substring(0, cursorPos);

            if (string2.charAt(0) == CommandManager.PREFIX.getValue().charAt(0)) {
                int j = 0;
                Matcher matcher = Pattern.compile("(\\s+)").matcher(string2);
                while (matcher.find()) {
                    j = matcher.end();
                }

                SuggestionsBuilder builder = new SuggestionsBuilder(string2, j);
                if (string2.length() <= prefix.length()) {
                    if (prefix.startsWith(string2)) {
                        builder.suggest(prefix + " ");
                    } else {
                        return;
                    }
                } else {
                    int count = StringUtils.countMatches(string2, " ");
                    List<String> seperated = Arrays.asList(string2.split(" "));
                    if (count == 1) {
                        for (Object strObj : Aoba.getInstance().commandManager.getCommands().keySet().toArray()) {
                            String str = (String) strObj;
                            builder.suggest(str + " ");
                        }
                    } else {
                        if (seperated.size() <= 1) return;
                        Command c = Aoba.getInstance().commandManager.getCommandBySyntax(seperated.get(1));
                        if (c == null) {
                            commandUsage.add(Component.nullToEmpty("Aoba: No commands found with name: " + string2).getVisualOrderText());
                            return;
                        }

                        String[] suggestions = c.getAutocorrect(seperated.get(seperated.size() - 1));

                        if (suggestions == null || suggestions.length == 0) return;
                        for (String str : suggestions) {
                            builder.suggest(str + " ");
                        }
                    }
                }

                pendingSuggestions = builder.buildFuture();
                showSuggestions(false);
            }
        }
    }
}
