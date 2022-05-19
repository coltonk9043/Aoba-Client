package net.aoba.gui.screens;

import net.aoba.Aoba;
import net.aoba.altmanager.Alt;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class EditAltScreen extends Screen {

	private final AltScreen parent;
	private Alt alt;

	private ButtonWidget buttonSaveAlt;
	private CheckboxWidget toggleMicrosoft;
	private TextFieldWidget textFieldAltUsername;
	private TextFieldWidget textFieldAltPassword;

	public EditAltScreen(AltScreen parentScreen, Alt alt) {
		super(new TranslatableText("Alt Manager"));
		this.parent = parentScreen;
		this.alt = alt;
	}

	public void init() {
		super.init();
		this.client.keyboard.setRepeatEvents(true);

		this.textFieldAltUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 76, 200, 20,
				new LiteralText("Enter Name"));
		this.textFieldAltUsername.setText(this.alt == null ? "" : alt.getEmail());
		this.addDrawableChild(this.textFieldAltUsername);

		this.textFieldAltPassword = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 36, 200, 20,
				new LiteralText("Enter Password"));
		this.textFieldAltPassword.setText(this.alt == null ? "" : alt.getPassword());
		this.addDrawableChild(this.textFieldAltPassword);

		this.toggleMicrosoft = new CheckboxWidget(this.width / 2 - 100, height / 2 - 12, 20, 20, new LiteralText("Microsoft Account?"), false);
		this.addDrawableChild(this.toggleMicrosoft);
		
		this.buttonSaveAlt = new ButtonWidget(this.width / 2 - 100, this.height / 2 + 24, 200, 20,
				new LiteralText("Save Alt"), b -> this.onButtonAltEditPressed());
		this.addDrawableChild(this.buttonSaveAlt);

		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 2 + 46, 200, 20,
				new LiteralText("Cancel"), b -> this.onButtonCancelPressed()));
	}

	@Override
	public void tick() {
		this.textFieldAltUsername.tick();
		this.textFieldAltPassword.tick();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		drawCenteredText(matrixStack, textRenderer, "Edit Alternate Account", this.width / 2, 20, 16777215);
		drawStringWithShadow(matrixStack, textRenderer, "Username:", this.width / 2 - 100, height / 2 - 90, 16777215);
		drawStringWithShadow(matrixStack, textRenderer, "Password:", this.width / 2 - 100, height / 2 - 50, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	private void onButtonAltEditPressed() {
		alt.setEmail(this.textFieldAltUsername.getText());
		alt.setPassword(this.textFieldAltPassword.getText());
		Aoba.getInstance().am.saveAlts();
		this.parent.refreshAltList();
	}

	public void onButtonCancelPressed() {
		client.setScreen(this.parent);
	}
}