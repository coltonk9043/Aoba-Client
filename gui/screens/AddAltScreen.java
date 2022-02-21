package net.aoba.gui.screens;

import net.aoba.Aoba;
import net.aoba.altmanager.Alt;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class AddAltScreen extends Screen{
	
	private final AltScreen parent;
	private Alt alt;
	
	private ButtonWidget buttonAddAlt;
	private TextFieldWidget textFieldAltUsername;
	private TextFieldWidget textFieldAltPassword;
	
	public AddAltScreen(AltScreen parentScreen, Alt alt) {
		super(new TranslatableText("Alt Manager"));
		this.parent = parentScreen;
		this.alt = alt;
	}
	
	public void init() {
		super.init();
		this.client.keyboard.setRepeatEvents(true);

		this.textFieldAltUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, 166, 200, 20, new LiteralText("Enter Name"));
		this.textFieldAltUsername.setText(this.alt == null ? "" : alt.getEmail());
		this.addDrawableChild(this.textFieldAltUsername);
		
		this.textFieldAltPassword = new TextFieldWidget(textRenderer, this.width / 2 - 100, 206, 200, 20, new LiteralText("Enter Password"));
		this.textFieldAltPassword.setText(this.alt == null ? "" : alt.getPassword());
		this.addDrawableChild(this.textFieldAltPassword);
		
		this.buttonAddAlt = new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, new LiteralText(this.alt == null ? "Add Alt" : "Save Alt"), b ->  this.onButtonAltAddPressed());
		this.addDrawableChild(this.buttonAddAlt);
		
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, new LiteralText("Cancel"), b -> client.setScreen(this.parent)));
	}
	
	@Override
	public void tick() {
	      this.textFieldAltUsername.tick();
	      this.textFieldAltPassword.tick();
	  }
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
		float partialTicks)
	{
		renderBackground(matrixStack);
		drawCenteredText(matrixStack, textRenderer, "Currently Logged Into: " + net.minecraft.client.MinecraftClient.getInstance().getSession().getUsername(), this.width / 2, 20, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	private void onButtonAltAddPressed() {
		   if(this.alt == null) {
			   Alt alt = new Alt(this.textFieldAltUsername.getText(), this.textFieldAltPassword.getText());
				  Aoba.getInstance().am.addAlt(alt);
			      //this.booleanConsumer.accept(true);
			      this.parent.refreshAltList();
		   }else {
			   alt.setEmail(this.textFieldAltUsername.getText());
			   alt.setPassword(this.textFieldAltPassword.getText());
			   Aoba.getInstance().am.saveAlts();
			   //this.booleanConsumer.accept(true);
			   this.parent.refreshAltList();
		   }
	   }
}
