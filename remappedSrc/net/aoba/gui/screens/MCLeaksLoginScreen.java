package net.aoba.gui.screens;

import net.aoba.Aoba;
import net.aoba.altmanager.exceptions.APIDownException;
import net.aoba.altmanager.exceptions.APIErrorException;
import net.aoba.altmanager.exceptions.InvalidResponseException;
import net.aoba.altmanager.exceptions.InvalidTokenException;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class MCLeaksLoginScreen extends Screen{

	private final Screen parent;
	private ButtonWidget buttonLoginAlt;
	
	private TextFieldWidget textFieldToken;
	
	private boolean didLoginError = false;
	
	protected MCLeaksLoginScreen(Screen parent) {
		super(Text.of("MCLeaks Login"));
		this.parent = parent;
	}

	public void init() {
		super.init();
		
		this.textFieldToken = new TextFieldWidget(textRenderer, this.width / 2 - 100, 206, 200, 20, Text.of("Enter MCLeaks Token"));
		this.addDrawableChild(this.textFieldToken);
		
		
		
		this.buttonLoginAlt = ButtonWidget.builder(Text.of("Login"), b ->  this.onButtonLoginPressed())
		 		.dimensions(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20).build();
		this.addDrawableChild(this.buttonLoginAlt);
		this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(this.parent))
		 		.dimensions(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20).build());
	}
	
	@Override
	public void tick() {
	      this.textFieldToken.tick();
	  }
	
	private void onButtonLoginPressed() {
		try {
			Aoba.getInstance().am.loginMCLeaks(this.textFieldToken.getText());
			client.setScreen(this.parent);
		} catch (APIDownException | APIErrorException | InvalidResponseException | InvalidTokenException e) {
			didLoginError = true;
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
			float partialTicks) {
		this.renderBackground(matrixStack);
		drawCenteredText(matrixStack, textRenderer, this.title.getString(), this.width / 2, 17, 16777215);
		drawStringWithShadow(matrixStack,textRenderer, "Enter Token", this.width / 2 - 100, 194, 10526880);
		this.textFieldToken.render(matrixStack,mouseX, mouseY, partialTicks);
		if (didLoginError) {
			drawStringWithShadow(matrixStack, textRenderer, "Incorrect Token", this.width / 2 - 140, 116, 0xFF0000);
		}
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}