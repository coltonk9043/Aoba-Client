package net.aoba.gui.screens;

import net.aoba.Aoba;
import net.aoba.altmanager.Alt;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class AddAltScreen extends Screen {

	private final AltScreen parent;

	private ButtonWidget buttonAddAlt;
	private CheckboxWidget toggleMicrosoft;
	private TextFieldWidget textFieldAltUsername;
	private TextFieldWidget textFieldAltPassword;

	public AddAltScreen(AltScreen parentScreen) {
		super(Text.of("Alt Manager"));
		this.parent = parentScreen;
	}

	public void init() {
		super.init();

		this.textFieldAltUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 76, 200, 20,
				Text.of("Enter Name"));
		this.textFieldAltUsername.setText("");
		this.addDrawableChild(this.textFieldAltUsername);

		this.textFieldAltPassword = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 36, 200, 20,
				Text.of("Enter Password"));
		this.textFieldAltPassword.setText("");
		this.addDrawableChild(this.textFieldAltPassword);
		textFieldAltPassword.setRenderTextProvider((text, n) -> {
			String str = "";
			for(int i = 0; i < text.length(); i++)
				str += "*";
			return OrderedText.styledForwardsVisitedString(str, Style.EMPTY);
		});
		this.toggleMicrosoft = new CheckboxWidget(this.width / 2 - 100, height / 2 + - 12, 20, 20, Text.of("Microsoft Account?"), false);
		this.addDrawableChild(this.toggleMicrosoft);
		
		this.buttonAddAlt = ButtonWidget.builder(Text.of("Add Alt"), b -> this.onButtonAltAddPressed())
		 		.dimensions(this.width / 2 - 100, this.height / 2 + 24, 200, 20).build();
		this.addDrawableChild(this.buttonAddAlt);
		
		this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> this.onButtonCancelPressed())
		 		.dimensions(this.width / 2 - 100, this.height / 2 + 46, 200, 20).build());
	}

	@Override
	public void tick() {
		this.textFieldAltUsername.tick();
		this.textFieldAltPassword.tick();
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		renderBackground(drawContext);
		drawContext.drawCenteredTextWithShadow(textRenderer, "Add Alternate Account", this.width / 2, 20, 16777215);
		drawContext.drawCenteredTextWithShadow(textRenderer, "Username:", this.width / 2 - 100, height / 2 - 90, 16777215);
		drawContext.drawCenteredTextWithShadow(textRenderer, "Password:", this.width / 2 - 100, height / 2 - 50, 16777215);
		super.render(drawContext, mouseX, mouseY, delta);
	}

	private void onButtonAltAddPressed() {
		Alt alt = new Alt(this.textFieldAltUsername.getText(), this.textFieldAltPassword.getText(), this.toggleMicrosoft.isChecked());
		Aoba.getInstance().am.addAlt(alt);
		this.parent.refreshAltList();
	}

	public void onButtonCancelPressed() {
		client.setScreen(this.parent);
	}
}
