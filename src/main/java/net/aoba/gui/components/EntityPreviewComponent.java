/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.colors.Colors;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class EntityPreviewComponent extends Component {
	private static final Identifier DEFAULT_PLAYER_SKIN = Identifier
			.withDefaultNamespace("textures/entity/player/wide/steve.png");

	// player entity face / hair/hat UV
	private static final float FACE_U0 = 8f / 64f;
	private static final float FACE_V0 = 8f / 64f;
	private static final float FACE_U1 = 16f / 64f;
	private static final float FACE_V1 = 16f / 64f;
	private static final float HAT_U0 = 40f / 64f;
	private static final float HAT_V0 = 8f / 64f;
	private static final float HAT_U1 = 48f / 64f;
	private static final float HAT_V1 = 16f / 64f;

	private final ItemPreviewComponent eggItemComponent;

	public static final UIProperty<EntityType<?>> EntityProperty = new UIProperty<>("Entity", null, false, false,
			EntityPreviewComponent::onEntityChanged);

	private static void onEntityChanged(UIElement sender, EntityType<?> oldValue, EntityType<?> newValue) {
		if (sender instanceof EntityPreviewComponent component) {
			component.updateStack(newValue);
		}
	}

	public EntityPreviewComponent() {
		eggItemComponent = new ItemPreviewComponent();
		setContent(eggItemComponent);
	}

	private void updateStack(@Nullable EntityType<?> type) {
		ItemStack stack = ItemStack.EMPTY;
		if (type != null && type != EntityType.PLAYER) {
			Optional<Holder<Item>> egg = SpawnEggItem.byId(type);
			if (egg.isPresent())
				stack = new ItemStack(egg.get());
		}
		eggItemComponent.setProperty(ItemPreviewComponent.ItemStackProperty, stack);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		// Players obviously do NOT have a player egg.
		// So we fall back to the Steve skin.
		EntityType<?> type = getProperty(EntityProperty);
		if (type == EntityType.PLAYER) {
			float ax = getActualSize().x();
			float ay = getActualSize().y();
			float aw = getActualSize().width();
			float ah = getActualSize().height();
			Shader passthrough = Shader.image(Colors.White);
			renderer.drawSprite(DEFAULT_PLAYER_SKIN, FACE_U0, FACE_V0, FACE_U1, FACE_V1, ax, ay, aw, ah, passthrough);
			renderer.drawSprite(DEFAULT_PLAYER_SKIN, HAT_U0, HAT_V0, HAT_U1, HAT_V1, ax, ay, aw, ah, passthrough);
			return;
		}
		super.draw(renderer, partialTicks);
	}
}
