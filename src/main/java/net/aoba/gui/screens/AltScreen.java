package net.aoba.gui.screens;

import java.util.List;
import net.aoba.Aoba;
import net.aoba.altmanager.Alt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class AltScreen extends Screen {
	private final Screen parentScreen;
	private ButtonWidget editButton;
	private ButtonWidget deleteButton;
	private AltSelectionList altListSelector;

	public AltScreen(Screen parentScreen) {
		super(Text.of("Alt Manager"));
		this.parentScreen = parentScreen;
	}

	public void init() {
		super.init();
		//this.client.keyboard.setRepeatEvents(true);

		this.altListSelector = new AltSelectionList(this, this.client, this.width, this.height, 32, this.height - 64,
				36);
		this.altListSelector.updateAlts();
		this.addDrawableChild(this.altListSelector);

		this.deleteButton = ButtonWidget.builder(Text.of("Delete Alt"), b -> this.deleteSelected())
				.dimensions(this.width / 2 - 154, this.height - 28, 100, 20).build();
		this.deleteButton.active = false;
		this.addDrawableChild(this.deleteButton);

		this.addDrawableChild(ButtonWidget.builder(Text.of("Direct Login"), b -> client.setScreen(new DirectLoginAltScreen(this)))
				.dimensions(this.width / 2 - 50, this.height - 52, 100, 20).build());

		
		this.addDrawableChild(ButtonWidget.builder(Text.of("Add Alt"), b -> client.setScreen(new AddAltScreen(this)))
				.dimensions(this.width / 2 + 4 + 50, this.height - 52, 100, 20).build());

		this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(this.parentScreen))
		 		.dimensions(this.width / 2 + 54, this.height - 28, 100, 20).build());
		
		
		this.editButton = ButtonWidget.builder(Text.of("Edit Alt"), b -> this.editSelected())
				.dimensions(this.width / 2 - 50, this.height - 28, 100, 20).build();
		this.editButton.active = false;
		this.addDrawableChild(this.editButton);

		
		this.addDrawableChild(ButtonWidget.builder(Text.of("MCLeaks Login"), b -> client.setScreen(new MCLeaksLoginScreen(this)))
		 		.dimensions(this.width / 2 - 154, this.height - 52, 100, 20).build());
	}

	@Override
	public void tick()
	{
		AltSelectionList.Entry altselectionlist$entry = this.altListSelector.getSelectedOrNull();
		if (altselectionlist$entry == null)
			return;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		this.altListSelector.render(matrixStack, mouseX, mouseY, partialTicks);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
		drawCenteredTextWithShadow(matrixStack, textRenderer,"Currently Logged Into: " + MinecraftClient.getInstance().getSession().getUsername(),
				this.width / 2, 20, 16777215);
	}

	public List<Alt> getAltList() {
		return Aoba.getInstance().am.getAlts();
	}

	public void refreshAltList() {
		this.client.setScreen(new AltScreen(this.parentScreen));
	}

	public void setSelected(AltSelectionList.Entry selected) {
		this.altListSelector.setSelected(selected);
		this.setEdittable();
	}
	
	protected void setEdittable() {
		this.editButton.active = true;
		this.deleteButton.active = true;
	}
	
	public void loginToSelected() {
		AltSelectionList.Entry altselectionlist$entry = this.altListSelector.getSelectedOrNull();
		if (altselectionlist$entry == null) {
			return;
		}
			
		Alt alt = ((AltSelectionList.NormalEntry) altselectionlist$entry).getAltData();
		if (alt.isCracked()) {
			Aoba.getInstance().am.loginCracked(alt.getEmail());
		} else {
			Aoba.getInstance().am.login(alt);
		}
	}
	
	public void editSelected() {
		Alt alt = ((AltSelectionList.NormalEntry)this.altListSelector.getSelectedOrNull()).getAltData();
		if (alt == null) {
			return;
		}
		client.setScreen(new EditAltScreen(this, alt));
	}
	
	public void deleteSelected() {
		Alt alt = ((AltSelectionList.NormalEntry)this.altListSelector.getSelectedOrNull()).getAltData();
		if (alt == null) {
			return;
		}
		Aoba.getInstance().am.removeAlt(alt);
		this.refreshAltList();
	}
}