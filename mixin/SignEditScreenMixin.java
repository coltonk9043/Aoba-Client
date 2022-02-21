package net.aoba.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.aoba.Aoba;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.world.AutoSign;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.text.Text;

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin extends Screen  {
	@Shadow
	@Final
	private String[] text;
	
	protected SignEditScreenMixin(Text title) {
		super(title);
	}
	
	@Inject(at = {@At("HEAD")}, method = {"init()V"})
	private void onInit(CallbackInfo ci)
	{
		AutoSign mod = (AutoSign) Aoba.getInstance().mm.autosign;
		String[] newText = mod.getText();
		if(newText != null) {
			for(int i = 0; i < 4; i++)
				text[i] = newText[i];
			finishEditing();
		}
	}
	
	@Inject(at = { @At("HEAD") }, method = "finishEditing()V")
	private void onEditorClose(CallbackInfo ci) {
		AutoSign mod = (AutoSign) Aoba.getInstance().mm.autosign;
		if(mod.getState()) {
			if(mod.getText() == null) {
				mod.setText(text);
				CommandManager.sendChatMessage("Sign text set!");
			}
		}
	}
	
	@Shadow
	private void finishEditing()
	{
		
	}
}
