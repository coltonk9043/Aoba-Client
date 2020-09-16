package aoba.main.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;

import aoba.main.altmanager.Alt;
import aoba.main.altmanager.AltManager;
import aoba.main.altmanager.exceptions.APIDownException;
import aoba.main.altmanager.exceptions.APIErrorException;
import aoba.main.altmanager.exceptions.InvalidResponseException;
import aoba.main.altmanager.exceptions.InvalidTokenException;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class MCLeaksLoginScreen extends Screen {
	private Button buttonLoginAlt;
	private final BooleanConsumer booleanConsumer;
	private TextFieldWidget textFieldToken;
	private final Screen parent;
	private boolean didLoginError = false;

	public MCLeaksLoginScreen(Screen parent, BooleanConsumer bool) {
		super(new TranslationTextComponent("MCLeaks Login"));
		this.parent = parent;
		this.booleanConsumer = bool;
	}

	public void tick() {
		this.textFieldToken.tick();
	}

	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.textFieldToken = new TextFieldWidget(this.font, this.width / 2 - 100, 206, 200, 20,
				new TranslationTextComponent("Enter Token"));
		this.textFieldToken.setMaxStringLength(20);
		this.textFieldToken.setText("");
		this.textFieldToken.setResponder(this::func_213028_a);
		this.children.add(this.textFieldToken);

		this.buttonLoginAlt = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20,
				new TranslationTextComponent("Login"), (p_213030_1_) -> {
					this.onButtonLoginPressed();
				}));
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20,
				new TranslationTextComponent("Cancel"), (p_214289_1_) -> {
					this.minecraft.displayGuiScreen(this.parent);
				}));
		this.func_228180_b_();
	}

	public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
		String s = this.textFieldToken.getText();
		this.init(p_resize_1_, p_resize_2_, p_resize_3_);
		this.textFieldToken.setText(s);
	}

	private void func_213028_a(String p_213028_1_) {
		this.func_228180_b_();
	}

	public void removed() {
		this.minecraft.keyboardListener.enableRepeatEvents(false);
	}

	private void onButtonLoginPressed() {
		try {
			AltManager.loginMCLeaks(this.textFieldToken.getText());
			this.booleanConsumer.accept(true);
		} catch (APIDownException | APIErrorException | InvalidResponseException | InvalidTokenException e) {
			e.printStackTrace();
		}
	}

	public void onClose() {
		this.func_228180_b_();
	}

	private void func_228180_b_() {
		String s = this.textFieldToken.getText();
		boolean flag = !s.isEmpty() && s.split(":").length > 0 && s.indexOf(32) == -1;
		this.buttonLoginAlt.active = flag && !this.textFieldToken.getText().isEmpty();
	}

	public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground(matrixStack);
		this.drawCenteredString(matrixStack, this.font, this.title.getString(), this.width / 2, 17, 16777215);
		this.drawString(matrixStack, this.font, I18n.format("Enter Token"), this.width / 2 - 100, 194, 10526880);
		this.textFieldToken.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
		if (didLoginError) {
			Minecraft.getInstance().fontRenderer.drawStringWithShadow(
					"Incorrect Login (Try using Email rather than Username)", this.width / 2 - 140, 116, 0xFF0000,
					true);
		}
		super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
	}
}