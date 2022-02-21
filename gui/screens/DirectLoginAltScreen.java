package net.aoba.gui.screens;

import net.aoba.Aoba;
import net.aoba.altmanager.Alt;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class DirectLoginAltScreen extends Screen{

	private final Screen parent;
	private ButtonWidget buttonLoginAlt;
	
	private TextFieldWidget textFieldAltUsername;
	private TextFieldWidget textFieldAltPassword;
	
	private boolean didLoginError = false;
	
	protected DirectLoginAltScreen(Screen parent) {
		super(new LiteralText("Direct Login"));
		this.parent = parent;
	}

	public void init() {
		super.init();
		this.client.keyboard.setRepeatEvents(true);

		this.textFieldAltUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, 166, 200, 20, new LiteralText("Enter Name"));
		this.addDrawableChild(this.textFieldAltUsername);
		
		this.textFieldAltPassword = new TextFieldWidget(textRenderer, this.width / 2 - 100, 206, 200, 20, new LiteralText("Enter Password"));
		this.addDrawableChild(this.textFieldAltPassword);
		
		this.buttonLoginAlt = new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, new LiteralText("Login"), b ->  this.onButtonLoginPressed());
		this.addDrawableChild(this.buttonLoginAlt);
		
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, new LiteralText("Cancel"), b -> client.setScreen(this.parent)));
	}
	
	@Override
	public void tick() {
	      this.textFieldAltUsername.tick();
	      this.textFieldAltPassword.tick();
	  }
	
	private void onButtonLoginPressed() {
		boolean loggedIn = false;
		if(this.textFieldAltPassword.getText().isEmpty()) {
			loggedIn = Aoba.getInstance().am.loginCracked(this.textFieldAltUsername.getText());
		}else {
			Alt alt = new Alt(this.textFieldAltUsername.getText(), this.textFieldAltPassword.getText());
			loggedIn = Aoba.getInstance().am.login(alt);
		}

		if(!loggedIn) {
			didLoginError = true;
		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
			float partialTicks) {
		this.renderBackground(matrixStack);
		drawCenteredText(matrixStack, textRenderer, this.title.getString(), this.width / 2, 17, 16777215);
		drawStringWithShadow(matrixStack, textRenderer, this.textFieldAltPassword.getText().isEmpty() ? "Cracked Account" : "Premium Account", this.width / 2 - 100, 138, this.textFieldAltPassword.getText().isEmpty() ? 0xFF0000 : 0x00FF00);
		drawStringWithShadow(matrixStack,textRenderer, "Enter Username", this.width / 2 - 100, 153, 10526880);
		drawStringWithShadow(matrixStack,textRenderer, "Enter Password", this.width / 2 - 100, 194, 10526880);
		this.textFieldAltUsername.render(matrixStack,mouseX, mouseY, partialTicks);
		this.textFieldAltPassword.render(matrixStack,mouseX, mouseY, partialTicks);
		if (didLoginError) {
			drawStringWithShadow(matrixStack, textRenderer, "Incorrect Login (Try using Email rather than Username)", this.width / 2 - 140, 116, 0xFF0000);
		}
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}
