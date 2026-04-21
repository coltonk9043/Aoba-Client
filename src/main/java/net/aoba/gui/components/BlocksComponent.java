/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.HashMap;
import java.util.HashSet;
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

public class BlocksComponent extends Component {
	private static final float BLOCK_SIZE = 40f;
	private static final float DEFAULT_HEIGHT = 128f;
	
	private static final Shader SELECTED_EFFECT = Shader.solid(new Color(0, 255, 0, 55));

	private HashSet<Block> blocks;
	
	// TODO: Change this to a UIProperty
	private BlocksSetting blocksSetting;
	private Consumer<HashSet<Block>> onChanged;

	private final WrapPanelComponent wrapPanel;
	private final HashMap<Block, RectangleComponent> cellByBlock = new HashMap<>();

	public BlocksComponent(HashSet<Block> blocks, Consumer<HashSet<Block>> onChanged) {
		this.blocks = blocks;
		this.onChanged = onChanged;
		ScrollComponent scroll = new ScrollComponent();
		WrapPanelComponent wp = new WrapPanelComponent();
		wp.setVirtualized(true);
		wp.setProperty(WrapPanelComponent.ItemSpacingProperty, 4f);
		wp.setProperty(WrapPanelComponent.RowSpacingProperty, 4f);
		scroll.setContent(wp);
		setContent(scroll);
		this.wrapPanel = wp;
		populate();
	}

	public BlocksComponent(BlocksSetting setting) {
		this.blocksSetting = setting;
		this.blocks = setting.getValue();
		setProperty(UIElement.HeightProperty, DEFAULT_HEIGHT);

		ScrollComponent scroll = new ScrollComponent();
		WrapPanelComponent wp = new WrapPanelComponent();
		wp.setVirtualized(true);
		wp.setProperty(WrapPanelComponent.ItemSpacingProperty, 4f);
		wp.setProperty(WrapPanelComponent.RowSpacingProperty, 4f);
		scroll.setContent(wp);
		setContent(scroll);
		this.wrapPanel = wp;
		populate();
		this.blocksSetting.addOnUpdate(settingListener);
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
			if (block == null)
				continue;
			wrapPanel.addChild(createCell(block));
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

		ItemPreviewComponent preview = new ItemPreviewComponent();
		preview.setProperty(ItemPreviewComponent.ItemProperty, block.asItem());
		cell.setContent(preview);

		cell.setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				toggleBlock(block, cell);
				e.cancel();
			}
		});

		cellByBlock.put(block, cell);
		return cell;
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
