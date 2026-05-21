/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import net.aoba.gui.UIElement;
import net.aoba.gui.colors.Color;
import net.aoba.gui.types.Thickness;
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Items;

public class BlocksComponent extends Component {
	private static final float BLOCK_SIZE = 40f;
	private static final float DEFAULT_HEIGHT = 128f;

	private static final Shader SELECTED_EFFECT = Shader.solid(new Color(0, 255, 0, 55));

	private HashSet<Block> blocks;

	// TODO: Change this to a UIProperty
	private BlocksSetting blocksSetting;
	private Consumer<HashSet<Block>> onChanged;

	private WrapPanelComponent wrapPanel;

	// We cache the valid blocks and their cells so we don't have to rebuild them on every keystroke
	private final List<Block> validBlocks = new ArrayList<>();
	private final HashMap<Block, RectangleComponent> cellByBlock = new HashMap<>();

	public BlocksComponent(HashSet<Block> blocks, Consumer<HashSet<Block>> onChanged) {
		this.blocks = blocks;
		this.onChanged = onChanged;
		initLayout();
	}

	public BlocksComponent(BlocksSetting setting) {
		this.blocksSetting = setting;
		this.blocks = setting.getValue();
		initLayout();
		this.blocksSetting.addOnUpdate(settingListener);
	}

	private void initLayout() {
		StackPanelComponent mainLayout = new StackPanelComponent();
		mainLayout.setSpacing(4f);

		TextBoxComponent searchTextBox = new TextBoxComponent();
		searchTextBox.setProperty(TextBoxComponent.PlaceholderText, "Search blocks...");
		searchTextBox.setOnTextChanged(this::onSearchTextChanged);
		mainLayout.addChild(searchTextBox);

		ScrollComponent scroll = new ScrollComponent();
		scroll.setProperty(UIElement.HeightProperty, DEFAULT_HEIGHT);

		this.wrapPanel = new WrapPanelComponent();
		this.wrapPanel.setVirtualized(true);
		this.wrapPanel.setProperty(WrapPanelComponent.ItemSpacingProperty, 4f);
		this.wrapPanel.setProperty(WrapPanelComponent.RowSpacingProperty, 4f);
		scroll.setContent(this.wrapPanel);

		mainLayout.addChild(scroll);
		setContent(mainLayout);

		populate();
		// Force the initial layout state
		onSearchTextChanged("");
	}

	private final Consumer<HashSet<Block>> settingListener = this::onSettingValueChanged;

	@Override
	public void dispose() {
		if (blocksSetting != null)
			blocksSetting.removeOnUpdate(settingListener);
		super.dispose();
	}

	private void populate() {
		int count = BuiltInRegistries.BLOCK.size();
		for (int i = 0; i < count; i++) {
			Block block = BuiltInRegistries.BLOCK.byId(i);
			if (block == null || block.asItem() == Items.AIR)
				continue;

			// Cache them in order instead of immediately adding them to the panel
			validBlocks.add(block);
			cellByBlock.put(block, createCell(block));
		}
	}

	private RectangleComponent createCell(Block block) {
		RectangleComponent cell = new RectangleComponent();
		cell.setProperty(UIElement.WidthProperty, BLOCK_SIZE);
		cell.setProperty(UIElement.HeightProperty, BLOCK_SIZE);
		cell.setProperty(RectangleComponent.CornerRadiusProperty, 0f);
		cell.setProperty(UIElement.PaddingProperty, new Thickness(6f));
		if (blocks.contains(block))
			cell.setProperty(UIElement.BackgroundProperty, SELECTED_EFFECT);

		cell.setProperty(UIElement.ToolTipProperty, block.getName().getString());

		ItemPreviewComponent preview = new ItemPreviewComponent();
		preview.setProperty(ItemPreviewComponent.ItemProperty, block.asItem());
		cell.setContent(preview);

		cell.setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				toggleBlock(block, cell);
				e.cancel();
			}
		});

		return cell;
	}

	private void onSearchTextChanged(String text) {
		String filter = (text == null) ? "" : text.toLowerCase().trim();

		// Clear all elements to force a fresh layout recalculation.
		wrapPanel.clearChildren();

		for (Block block : validBlocks) {
			String localizedName = block.getName().getString().toLowerCase();
			String registryPath = BuiltInRegistries.BLOCK.getKey(block).getPath().toLowerCase();

			if (filter.isEmpty() || localizedName.contains(filter) || registryPath.contains(filter)) {
				// Only append cells that match, forcing the wrap panel to reflow them to the top
				wrapPanel.addChild(cellByBlock.get(block));
			}
		}
	}

	private void toggleBlock(Block block, RectangleComponent cell) {
		if (blocks.contains(block)) {
			blocks.remove(block);
			cell.setProperty(UIElement.BackgroundProperty, null);
		} else {
			blocks.add(block);
			cell.setProperty(UIElement.BackgroundProperty, SELECTED_EFFECT);
		}

		if (blocksSetting != null)
			blocksSetting.setValue(blocks);
		if (onChanged != null)
			onChanged.accept(blocks);
	}

	private void onSettingValueChanged(HashSet<Block> b) {
		if (b == this.blocks)
			return;
		this.blocks = b;
		for (var entry : cellByBlock.entrySet()) {
			Shader bg = blocks.contains(entry.getKey()) ? SELECTED_EFFECT : null;
			entry.getValue().setProperty(UIElement.BackgroundProperty, bg);
		}
	}

	public HashSet<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(HashSet<Block> blocks) {
		if (this.blocks == blocks)
			return;
		this.blocks = blocks;
		if (blocksSetting != null)
			blocksSetting.setValue(blocks);
		for (var entry : cellByBlock.entrySet()) {
			Shader bg = blocks.contains(entry.getKey()) ? SELECTED_EFFECT : null;
			entry.getValue().setProperty(UIElement.BackgroundProperty, bg);
		}
	}
}