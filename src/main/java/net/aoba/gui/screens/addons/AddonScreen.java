package net.aoba.gui.screens.addons;

import java.nio.file.Path;
import java.util.List;

import net.aoba.api.IAddon;
import net.aoba.gui.screens.addons.AddonSelectionList.NormalEntry;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;

public class AddonScreen extends Screen {
	protected static final CubeMapRenderer AOBA_PANORAMA_RENDERER = new CubeMapRenderer(TextureBank.mainmenu_panorama);
	protected static final RotatingCubeMapRenderer AOBA_ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(
			AOBA_PANORAMA_RENDERER);
	private final Screen parentScreen;

	// Widget
	private AddonSelectionList addonListSelector;
	private TextFieldWidget descriptionWidget;

	// Selected Mod
	private IAddon selectedAddon;

	public AddonScreen(Screen parentScreen) {
		super(Text.of("Addon Manager"));
		this.parentScreen = parentScreen;
	}

	public void init() {
		super.init();

		// Left Side
		Path addonsPath = MinecraftClient.getInstance().getResourcePackDir().getParent().resolve("mods");
		this.addDrawableChild(ButtonWidget
				.builder(Text.of("Open Addons Folder"), button -> Util.getOperatingSystem().open(addonsPath))
				.dimensions(16, this.height - 40, 120, 20).build());

		this.addDrawableChild(ButtonWidget.builder(Text.of("Done"), b -> client.setScreen(this.parentScreen))
				.dimensions(146, this.height - 40, 120, 20).build());

		this.addonListSelector = new AddonSelectionList(this, this.client, 250, this.height, 16, 48);
		this.addonListSelector.setDimensionsAndPosition(250, this.height - 80, 16, 32);
		this.addDrawableChild(this.addonListSelector);

		// Right Side
		this.descriptionWidget = new TextFieldWidget(textRenderer, 282, 112, this.width - 314, this.height - 160,
				Text.of(""));
		descriptionWidget.setEditable(false);
		descriptionWidget.setFocusUnlocked(false);
		this.addDrawableChild(this.descriptionWidget);

		// Set the selected addon if any exist.
		List<NormalEntry> entries = addonListSelector.getAddons();
		if (entries.size() > 0) {
			setSelected(entries.getFirst());
		}
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(drawContext, mouseX, mouseY, partialTicks);

		super.render(drawContext, mouseX, mouseY, partialTicks);
		drawContext.drawCenteredTextWithShadow(textRenderer, "Addons", 145, 16, 16777215);

		// Draw Addon Information
		if (selectedAddon != null) {
			// Although selected addon is TECHNICALLY what we need... we need an icon
			// identifier :(
			AddonSelectionList.NormalEntry entry = (AddonSelectionList.NormalEntry) addonListSelector
					.getSelectedOrNull();
			if (entry != null) {
				drawContext.drawTexture(RenderLayer::getGuiTextured, entry.getIcon(), 282, 68, 0, 0, 48, 48, 48, 48);
			}
			drawContext.drawTextWithShadow(textRenderer, selectedAddon.getName(), 338, 68, 16777215);
			drawContext.drawTextWithShadow(textRenderer, selectedAddon.getVersion(), 338, 80, 16777215);
			drawContext.drawTextWithShadow(textRenderer, "By " + selectedAddon.getAuthor(), 338, 92, 16777215);
		}
	}

	public void setSelected(AddonSelectionList.NormalEntry selected) {
		this.addonListSelector.setSelected(selected);
		selectedAddon = selected.getAddon();

		if (descriptionWidget != null) {
			if (selectedAddon != null) {
				descriptionWidget.visible = true;
				descriptionWidget.setUneditableColor(Colors.WHITE);
				descriptionWidget.setText(selectedAddon.getDescription());
			} else
				descriptionWidget.visible = false;
		}
	}

	@Override
	protected void renderPanoramaBackground(DrawContext context, float delta) {
		AOBA_ROTATING_PANORAMA_RENDERER.render(context, this.width, this.height, 1.0f, delta);
	}
}
