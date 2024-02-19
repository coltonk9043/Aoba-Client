package net.aoba.gui.screens;

import net.aoba.Aoba;
import net.aoba.altmanager.Alt;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class DirectLoginAltScreen extends Screen{

	private final Screen parent;
	private ButtonWidget buttonLoginAlt;
	private CheckboxWidget toggleMicrosoft;
	
	private TextFieldWidget textFieldAltUsername;
	private TextFieldWidget textFieldAltPassword;
	
	private boolean didLoginError = false;
	
	protected DirectLoginAltScreen(Screen parent) {
		super(Text.of("Direct Login"));
		this.parent = parent;
	}

	public void init() {
		super.init();

		this.textFieldAltUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 76, 200, 20, Text.of("Enter Name"));
		this.addDrawableChild(this.textFieldAltUsername);
		
		this.textFieldAltPassword = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 36, 200, 20, Text.of("Enter Password"));
		textFieldAltPassword.setRenderTextProvider((text, n) -> {
			String str = "";
			for(int i = 0; i < text.length(); i++)
				str += "*";
			return OrderedText.styledForwardsVisitedString(str, Style.EMPTY);
		});
		this.addDrawableChild(this.textFieldAltPassword);
		
		this.toggleMicrosoft = CheckboxWidget.builder(Text.of("Microsoft Account?"), textRenderer).pos(this.width / 2 - 100, height / 2 + - 12).build();
		this.addDrawableChild(this.toggleMicrosoft);
		
		this.buttonLoginAlt = ButtonWidget.builder(Text.of("Login"), b ->  this.onButtonLoginPressed())
		 		.dimensions(this.width / 2 - 100, this.height / 2 + 24, 200, 20).build();
		this.addDrawableChild(this.buttonLoginAlt);
		
		this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(this.parent))
		 		.dimensions(this.width / 2 - 100, this.height / 2 + 46, 200, 20).build());
	}
	
	@Override
	public void tick() {
		// TODO:
	     // this.textFieldAltUsername.tick();
	      //this.textFieldAltPassword.tick();
	  }
	
	private void onButtonLoginPressed() {
		boolean loggedIn = false;
		if(this.textFieldAltPassword.getText().isEmpty()) {
			Aoba.getInstance().altManager.loginCracked(this.textFieldAltUsername.getText());
			client.setScreen(this.parent);
			return;
		}else {
			Alt alt = new Alt(this.textFieldAltUsername.getText(), this.textFieldAltPassword.getText(), this.toggleMicrosoft.isChecked());
			loggedIn = Aoba.getInstance().altManager.login(alt);
		}

		if(!loggedIn) {
			didLoginError = true;
		}else {
			client.setScreen(this.parent);
		}
	}
	
	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(drawContext, mouseX, mouseY, partialTicks);
		drawContext.drawCenteredTextWithShadow(textRenderer, this.title.getString(), this.width / 2, 20, 16777215);
		drawContext.drawTextWithShadow(textRenderer, this.textFieldAltPassword.getText().isEmpty() ? "Cracked Account" : "Premium Account", this.width / 2 - 100, height / 2 - 106, this.textFieldAltPassword.getText().isEmpty() ? 0xFF0000 : 0x00FF00);
		drawContext.drawTextWithShadow(textRenderer, "Enter Username", this.width / 2 - 100, height / 2 - 90, 16777215);
		drawContext.drawTextWithShadow(textRenderer, "Enter Password", this.width / 2 - 100, height / 2 - 50, 16777215);
		//drawStringWithShadow(matrixStack,textRenderer, "Microsoft: ", this.width / 2 - 100, height / 2 - 10, 16777215);
		this.textFieldAltUsername.render(drawContext,mouseX, mouseY, partialTicks);
		this.textFieldAltPassword.render(drawContext,mouseX, mouseY, partialTicks);
		if (didLoginError) {
			drawContext.drawTextWithShadow(textRenderer, "Incorrect Login (Try using Email rather than Username)", this.width / 2 - 140, 116, 0xFF0000);
		}
		super.render(drawContext, mouseX, mouseY, partialTicks);
	}
}
