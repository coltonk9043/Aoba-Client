package aoba.main.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;

import aoba.main.altmanager.Alt;
import aoba.main.gui.widgets.PasswordFieldWidget;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class DirectLoginAltScreen extends Screen {
	private Button buttonLoginAlt;
	private final BooleanConsumer booleanConsumer;
	private PasswordFieldWidget textFieldAltPassword;
	private TextFieldWidget textFieldAltUsername;
	private final Screen parent;
	private boolean didLoginError = false;

	public DirectLoginAltScreen(Screen parent, BooleanConsumer bool) {
		super(new TranslationTextComponent("Direct Login"));
		this.parent = parent;
		this.booleanConsumer = bool;
	}

	public void tick() {
		this.textFieldAltUsername.tick();
		this.textFieldAltPassword.tick();
	}

	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.textFieldAltUsername = new TextFieldWidget(this.font, this.width / 2 - 100, 166, 200, 20,
				new TranslationTextComponent("Enter Username"));
		this.textFieldAltUsername.setFocused2(true);
		this.textFieldAltUsername.setText("");
		this.textFieldAltUsername.setResponder(this::func_213028_a);
		this.children.add(this.textFieldAltUsername);
		this.textFieldAltPassword = new PasswordFieldWidget(this.font, this.width / 2 - 100, 206, 200, 20,
				new TranslationTextComponent("Enter Password"));
		this.textFieldAltPassword.setMaxStringLength(128);
		this.textFieldAltPassword.setText("");
		this.textFieldAltPassword.setResponder(this::func_213028_a);
		this.children.add(this.textFieldAltPassword);

		this.buttonLoginAlt = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20,
				new TranslationTextComponent("Login"), (p_213030_1_) -> {
					this.onButtonLoginPressed();
				}));
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, new TranslationTextComponent("Cancel"),
				(p_214289_1_) -> {
					this.minecraft.displayGuiScreen(this.parent);
				}));
		this.func_228180_b_();
	}

	public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
		String s = this.textFieldAltPassword.getText();
		String s1 = this.textFieldAltUsername.getText();
		this.init(p_resize_1_, p_resize_2_, p_resize_3_);
		this.textFieldAltPassword.setText(s);
		this.textFieldAltUsername.setText(s1);
	}

	private void func_213028_a(String p_213028_1_) {
		this.func_228180_b_();
	}

	public void removed() {
		this.minecraft.keyboardListener.enableRepeatEvents(false);
	}

	private void onButtonLoginPressed() {
		boolean loggedIn = false;
		if(this.textFieldAltPassword.getText().isEmpty()) {
			loggedIn = Minecraft.getInstance().aoba.am.loginCracked(this.textFieldAltUsername.getText());
		}else {
			Alt alt = new Alt(this.textFieldAltUsername.getText(), this.textFieldAltPassword.getText());
			loggedIn = Minecraft.getInstance().aoba.am.login(alt);
		}

		if(loggedIn) {
			this.booleanConsumer.accept(true);
		}else {
			didLoginError = true;
		}
	}

	public void onClose() {
		this.func_228180_b_();
	}

	private void func_228180_b_() {
		String s = this.textFieldAltUsername.getText();
		boolean flag = !s.isEmpty() && s.split(":").length > 0 && s.indexOf(32) == -1;
		this.buttonLoginAlt.active = flag && !this.textFieldAltUsername.getText().isEmpty();
	}

	public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground(matrixStack);
		drawCenteredString(matrixStack, this.font, this.title.getString(), this.width / 2, 17, 16777215);
		drawString(matrixStack, this.font, this.textFieldAltPassword.getText().isEmpty() ? I18n.format("Cracked Account") : I18n.format("Premium Account"), this.width / 2 - 100, 138, this.textFieldAltPassword.getText().isEmpty() ? 0xFF0000 : 0x00FF00);
		drawString(matrixStack,this.font, I18n.format("Enter Username"), this.width / 2 - 100, 153, 10526880);
		drawString(matrixStack,this.font, I18n.format("Enter Password"), this.width / 2 - 100, 194, 10526880);
		this.textFieldAltUsername.render(matrixStack,p_render_1_, p_render_2_, p_render_3_);
		this.textFieldAltPassword.render(matrixStack,p_render_1_, p_render_2_, p_render_3_);
		if (didLoginError) {
			Minecraft.getInstance().fontRenderer.drawStringWithShadow(
					"Incorrect Login (Try using Email rather than Username)", this.width / 2 - 140, 116, 0xFF0000, true);
		}
		super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
	}
}