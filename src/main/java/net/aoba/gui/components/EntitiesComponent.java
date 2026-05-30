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
import java.util.Set;
import java.util.function.Consumer;

import net.aoba.gui.UIElement;
import net.aoba.gui.colors.Color;
import net.aoba.gui.types.Thickness;
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.EntitiesSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntitiesComponent extends Component {
	private static final float ENTITY_SIZE = 40f;
	private static final float DEFAULT_HEIGHT = 128f;

	private static final Shader SELECTED_EFFECT = Shader.solid(new Color(0, 255, 0, 55));

	private Set<EntityType<?>> entities;

	private EntitiesSetting entitiesSetting;
	private Consumer<Set<EntityType<?>>> onChanged;
	private WrapPanelComponent wrapPanel;
	private final List<EntityType<?>> validEntities = new ArrayList<>();
	private final HashMap<EntityType<?>, RectangleComponent> cellByEntity = new HashMap<>();

	public EntitiesComponent(Set<EntityType<?>> entities, Consumer<Set<EntityType<?>>> onChanged) {
		this.entities = entities;
		this.onChanged = onChanged;
		initLayout();
	}

	public EntitiesComponent(EntitiesSetting setting) {
		this.entitiesSetting = setting;
		this.entities = setting.getValue();
		initLayout();
		this.entitiesSetting.addOnUpdate(settingListener);
	}

	private void initLayout() {
		StackPanelComponent mainLayout = new StackPanelComponent();
		mainLayout.setSpacing(4f);

		TextBoxComponent searchTextBox = new TextBoxComponent();
		searchTextBox.setProperty(TextBoxComponent.PlaceholderText, "Search entities...");
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
		onSearchTextChanged("");
	}

	private final Consumer<Set<EntityType<?>>> settingListener = this::onSettingValueChanged;

	@Override
	public void dispose() {
		if (entitiesSetting != null)
			entitiesSetting.removeOnUpdate(settingListener);
		super.dispose();
	}

	private void populate() {
		validEntities.add(EntityType.PLAYER);
		cellByEntity.put(EntityType.PLAYER, createCell(EntityType.PLAYER));

		int count = BuiltInRegistries.ENTITY_TYPE.size();
		for (int i = 0; i < count; i++) {
			EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.byId(i);
			if (entityType == null || entityType == EntityType.PLAYER)
				continue;

			if (entityType.getCategory() == MobCategory.MISC)
				continue;

			validEntities.add(entityType);
			cellByEntity.put(entityType, createCell(entityType));
		}
	}

	private RectangleComponent createCell(EntityType<?> entityType) {
		RectangleComponent cell = new RectangleComponent();
		cell.setProperty(UIElement.WidthProperty, ENTITY_SIZE);
		cell.setProperty(UIElement.HeightProperty, ENTITY_SIZE);
		cell.setProperty(RectangleComponent.CornerRadiusProperty, 0f);
		cell.setProperty(UIElement.PaddingProperty, new Thickness(6f));
		if (entities.contains(entityType))
			cell.setProperty(UIElement.BackgroundProperty, SELECTED_EFFECT);

		cell.setProperty(UIElement.ToolTipProperty, entityType.getDescription().getString());

		EntityPreviewComponent preview = new EntityPreviewComponent();
		preview.setProperty(EntityPreviewComponent.EntityProperty, entityType);
		preview.setProperty(UIElement.IsHitTestVisibleProperty, false);
		cell.setContent(preview);

		cell.setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				toggleEntity(entityType, cell);
				e.cancel();
			}
		});

		return cell;
	}

	private void onSearchTextChanged(String text) {
		String filter = (text == null) ? "" : text.toLowerCase().trim();

		wrapPanel.clearChildren();

		for (EntityType<?> entityType : validEntities) {
			String localizedName = entityType.getDescription().getString().toLowerCase();
			String registryPath = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath().toLowerCase();

			if (filter.isEmpty() || localizedName.contains(filter) || registryPath.contains(filter)) {
				wrapPanel.addChild(cellByEntity.get(entityType));
			}
		}
	}

	private void toggleEntity(EntityType<?> entityType, RectangleComponent cell) {
		HashSet<EntityType<?>> newEntities = new HashSet<>(entities);
		if (newEntities.contains(entityType)) {
			newEntities.remove(entityType);
			cell.setProperty(UIElement.BackgroundProperty, null);
		} else {
			newEntities.add(entityType);
			cell.setProperty(UIElement.BackgroundProperty, SELECTED_EFFECT);
		}
		this.entities = newEntities;

		if (entitiesSetting != null)
			entitiesSetting.setValue(newEntities);
		if (onChanged != null)
			onChanged.accept(newEntities);
	}

	private void onSettingValueChanged(Set<EntityType<?>> e) {
		if (e == this.entities)
			return;
		this.entities = e;
		for (var entry : cellByEntity.entrySet()) {
			Shader bg = entities.contains(entry.getKey()) ? SELECTED_EFFECT : null;
			entry.getValue().setProperty(UIElement.BackgroundProperty, bg);
		}
	}

	public Set<EntityType<?>> getEntities() {
		return entities;
	}

	public void setEntities(Set<EntityType<?>> entities) {
		if (this.entities == entities)
			return;
		this.entities = entities;
		if (entitiesSetting != null)
			entitiesSetting.setValue(entities);
		for (var entry : cellByEntity.entrySet()) {
			Shader bg = entities.contains(entry.getKey()) ? SELECTED_EFFECT : null;
			entry.getValue().setProperty(UIElement.BackgroundProperty, bg);
		}
	}
}
