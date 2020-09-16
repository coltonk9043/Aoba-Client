package aoba.main.gui.screens;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import aoba.main.altmanager.Alt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class AltScreen extends Screen {
	private final Screen parentScreen;
	private Button editButton;
	private Button deleteButton;
	protected AltSelectionList altListSelector;
	private List<Alt> savedAltList = new ArrayList<Alt>();

	public AltScreen(Screen parentScreen) {
		super(new TranslationTextComponent("Alt Manager"));
		this.parentScreen = parentScreen;
		for (Alt alt : Minecraft.getInstance().aoba.am.getAlts()) {
			this.savedAltList.add(alt);
		}
	}

	public List<Alt> getAltList() {
		return this.savedAltList;
	}

	protected void init() {
		super.init();
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.deleteButton = new Button(this.width / 2 - 154, this.height - 28, 100, 20, new TranslationTextComponent("Delete Alt"),
				(p_214286_1_) -> {
					Alt altToRemove = ((AltSelectionList.NormalEntry)this.altListSelector.getSelected()).getAltData();
					Minecraft.getInstance().aoba.am.removeAlt(altToRemove);
					this.refreshAltList();
				});
		this.deleteButton.active = false;
		this.addButton(this.deleteButton);
		this.addButton(new Button(this.width / 2 - 50, this.height - 52, 100, 20, new TranslationTextComponent("Direct Login"),
				(p_214286_1_) -> {
					this.minecraft.displayGuiScreen(new DirectLoginAltScreen(this, this::func_214284_c));
				}));
		this.addButton(new Button(this.width / 2 + 4 + 50, this.height - 52, 100, 20, new TranslationTextComponent("Add Alt"),
				(p_214288_1_) -> {
					this.minecraft.displayGuiScreen(new AddAltScreen(this, this::func_214284_c, null));
				}));
		this.addButton(new Button(this.width / 2 + 54, this.height - 28, 100, 20, new TranslationTextComponent("gui.cancel"),
				(p_214289_1_) -> {
					this.minecraft.displayGuiScreen(this.parentScreen);
				}));
		this.editButton = new Button(this.width / 2 - 50, this.height - 28, 100, 20, new TranslationTextComponent("Edit Alt"),
				(p_214289_1_) -> {
					AltSelectionList.Entry altselectionlist$entry = this.altListSelector.getSelected();
						Alt alt = ((AltSelectionList.NormalEntry)altselectionlist$entry).getAltData();
				        this.minecraft.displayGuiScreen(new AddAltScreen(this, this::func_214284_c, alt));
					
				});
		this.editButton.active = false;
		this.addButton(this.editButton);
		this.addButton(new Button(this.width / 2 - 154, this.height - 52, 100, 20, new TranslationTextComponent("MCLeaks Login"),
				(p_214289_1_) -> {
					this.minecraft.displayGuiScreen(new MCLeaksLoginScreen(this, this::func_214284_c));
				}));
		this.altListSelector = new AltSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
		this.altListSelector.updateAlts();
		this.children.add(this.altListSelector);
	}

	public void tick() {
		super.tick();
	}

	public void loginToSelected() {
		AltSelectionList.Entry altselectionlist$entry = this.altListSelector.getSelected();
		Alt alt = ((AltSelectionList.NormalEntry) altselectionlist$entry).getAltData();
		if (alt.isCracked()) {
			Minecraft.getInstance().aoba.am.loginCracked(alt.getUsername());
		} else {
			Minecraft.getInstance().aoba.am.login(alt);
		}

	}

	public void removed() {
		this.minecraft.keyboardListener.enableRepeatEvents(false);
	}

	private void func_214284_c(boolean p_214284_1_) {
		this.minecraft.displayGuiScreen(this);
	}

	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
			return true;
		} else {
			return false;
		}
	}

	public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground(matrixStack);
		this.altListSelector.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
		drawCenteredString(matrixStack, this.font, "Currently Logged Into: " + Minecraft.getInstance().session.getUsername(), this.width / 2, 20, 16777215);
		super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
	}

	public void func_214287_a(AltSelectionList.Entry p_214287_1_) {
		this.altListSelector.setSelected(p_214287_1_);
		this.func_214295_b();
	}

	public void refreshAltList() {
		this.minecraft.displayGuiScreen(new AltScreen(this.parentScreen));
	}

	private void func_214285_a(boolean p_214285_1_) {
		AltSelectionList.Entry altselectionlist$entry = this.altListSelector.getSelected();

		if (p_214285_1_ && altselectionlist$entry instanceof AltSelectionList.NormalEntry) {
			this.altListSelector.setSelected((AltSelectionList.Entry) null);
		}

		this.minecraft.displayGuiScreen(this);
	}

	protected void func_214295_b() {
		this.editButton.active = true;
		this.deleteButton.active = true;
	}
}
