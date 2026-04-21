/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.UIProperty.CoerseCallback;
import net.aoba.gui.font.FontManager;
import net.aoba.gui.font.UIFont;
import net.aoba.gui.types.BindingMode;
import net.aoba.gui.types.HorizontalAlignment;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.Size;
import net.aoba.gui.types.Thickness;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.settings.Setting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.minecraft.client.Minecraft;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;

public abstract class UIElement {
	protected static Minecraft MC = Minecraft.getInstance();
	protected static AobaClient AOBA = Aoba.getInstance();

	private HashMap<UIProperty<?>, Object> localProperties;
	private List<PropertyBinding<?>> propertyBindings;
	
	protected UIElement parent;
	protected boolean measureDirty = true;
	private Float lastMeasureWidth = null;
	private Float lastMeasureHeight = null;
	protected boolean initialized = false;
	protected Size preferredSize;
	protected Rectangle actualSize;

	public static final UIProperty<Float> BorderThicknessProperty = new UIProperty<>("BorderThickness", 0f);
	public static final UIProperty<Float> CornerRadiusProperty = new UIProperty<>("CornerRadius", 6f);
	public static final UIProperty<Shader> BackgroundProperty = new UIProperty<>("Background", null, false);
	public static final UIProperty<Shader> BorderProperty = new UIProperty<>("Border", null, false);
	public static final UIProperty<Shader> ForegroundProperty = new UIProperty<>("Foreground", null, true);
	public static final UIProperty<CursorStyle> CursorProperty = new UIProperty<>("Cursor", null, true, false);
	public static final UIProperty<Boolean> IsHoveredProperty = new UIProperty<>("IsHovered", false);
	public static final UIProperty<Boolean> IsVisibleProperty = new UIProperty<>("IsVisible", true, true, true, UIElement::onVisibilityPropertyChanged);
	public static final UIProperty<Boolean> ClipToBoundsProperty = new UIProperty<>("ClipToBounds", false);
	public static final UIProperty<Boolean> IsHitTestVisibleProperty = new UIProperty<>("IsHitTestVisible", true);
	public static final UIProperty<String> ToolTipProperty = new UIProperty<>("ToolTip", null);
	public static final UIProperty<Thickness> MarginProperty = new UIProperty<>("Margin", new Thickness(0,0,0,0));
	public static final UIProperty<Thickness> PaddingProperty = new UIProperty<>("Padding", new Thickness(0,0,0,0));
	public static final UIProperty<HorizontalAlignment> HorizontalAlignmentProperty = new UIProperty<>("HorizontalAlignment", HorizontalAlignment.Stretch);
	public static final UIProperty<VerticalAlignment> VerticalAlignmentProperty = new UIProperty<>("VerticalAlignment", VerticalAlignment.Stretch);
	public static final UIProperty<Float> WidthProperty = new UIProperty<>("Width", null, false, true);
	public static final UIProperty<Float> HeightProperty = new UIProperty<>("Height", null, false, true);
	public static final UIProperty<Float> MaxWidthProperty = new UIProperty<>("MaxWidth", null, false, true);
	public static final UIProperty<Float> MaxHeightProperty = new UIProperty<>("MaxHeight", null, false, true);
	public static final UIProperty<Float> MinWidthProperty = new UIProperty<>("MinWidth", null, false, true);
	public static final UIProperty<Float> MinHeightProperty = new UIProperty<>("MinHeight", null, false, true);
	public static final UIProperty<UIFont> FontProperty = new UIProperty<>("Font", Aoba.getInstance().fontManager.getDefaultFont(), true, true);
	public static final UIProperty<Integer> FontWeightProperty = new UIProperty<>("FontWeight", FontManager.WEIGHT_NORMAL, true, true);
	
	private Consumer<MouseClickEvent> onClicked;

	public UIElement() {
		preferredSize = new Size(0.0f, 0.0f);
		actualSize = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
	}

	private static void onVisibilityPropertyChanged(UIElement sender, Boolean oldValue, Boolean newValue) {
		sender.onVisibilityChanged(oldValue, newValue);
	}
	
	protected void onVisibilityChanged(Boolean oldValue, Boolean newValue) {
		
	}
	
	/**
	 * Initializes the UI element.
	 */
	public void initialize() {
		boolean wasInitialized = initialized;
		if (!wasInitialized) {
			initialized = true;
		}

		if (!wasInitialized) {
			onInitialized();
			invalidateMeasure();
		}
	}

	/**
	 * Returns whether the UI element is initialized.
	 * @return True when initialized, false otherwise.
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Fires when the UI element is initialized.
	 */
	protected void onInitialized() {
	}

	/**
	 * Updates the UI element per tick.
	 */
	public void update() {
	}

	/**
	 * Draws the UI element on the screen.
	 * @param renderer     The 2D renderer for this frame.
	 * @param partialTicks Partial Ticks of the game.
	 */
	public void draw(Renderer2D renderer, float partialTicks) {
		
	}

	public void dispose() {
		if (propertyBindings != null) {
			for (PropertyBinding<?> binding : propertyBindings)
				binding.unbind();
			propertyBindings = null;
		}
	}

	/**
	 * Gets the preferred size of the UI element.
	 * @return Preferred size of the UI element.
	 */
	public Size getPreferredSize() {
		return preferredSize;
	}

	/**
	 * Gets the actual size of the UI element.
	 * @return Actual size of the UI element.
	 */
	public Rectangle getActualSize() {
		return actualSize;
	}

	/**
	 * Gets the content area of the UI element (actualSize inset by padding).
	 * This is the area available to children.
	 * @return Content area as a Rectangle.
	 */
	public Rectangle getContentArea() {
		Rectangle area = getActualSize();
		Thickness padding = getProperty(UIElement.PaddingProperty);
		if (padding != null) {
			area = new Rectangle(
				area.x() + padding.left(),
				area.y() + padding.top(),
				area.width() - padding.horizontalSum(),
				area.height() - padding.verticalSum()
			);
		}
		return area;
	}

	/**
	 * Sets the actual size of the UI element.
	 * @param actualSize Size to set the UI element to.
	 */
	protected void setActualSize(Rectangle actualSize) {
		this.actualSize = actualSize;
	}

	/**
	 * Sets the static size of the UI element.
	 * @param size New size of the UI element.
	 */
	public void setSize(Size size) {
		setProperty(UIElement.WidthProperty, size.width());
		setProperty(UIElement.HeightProperty, size.height());
	}

	/**
	 * Sets the static size of the UI element.
	 * @param width New width of the UI element.
	 * @param height New height of the UI element.
	 */
	public void setSize(Float width, Float height) {
		setProperty(UIElement.WidthProperty, width);
		setProperty(UIElement.HeightProperty, height);
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperty(UIProperty<T> property) {
		if (localProperties != null && localProperties.containsKey(property))
			return (T) localProperties.get(property);
		if (property.inherits() && parent != null)
			return parent.getProperty(property);
		return property.getDefaultValue();
	}

	public boolean hasLocalProperty(UIProperty<?> property) {
		return localProperties != null && localProperties.containsKey(property);
	}

	@SuppressWarnings("unchecked")
	public <T> void setProperty(UIProperty<T> property, T value) {
		if (localProperties == null)
			localProperties = new HashMap<>();

		CoerseCallback<T> coerce = property.getCoerceCallback();
		if (coerce != null)
			value = coerce.coerce(this, value);

		T oldValue = (T) localProperties.get(property);
		if (oldValue == null && !localProperties.containsKey(property))
			oldValue = property.getDefaultValue();

		if (Objects.equals(oldValue, value))
			return;

		localProperties.put(property, value);
		onPropertyChanged(property, oldValue, value);

		if (propertyBindings != null) {
			for (PropertyBinding<?> binding : propertyBindings) {
				if (binding.getProperty() == property && binding.getMode() == BindingMode.TwoWay)
					binding.pushToSetting(value);
			}
		}
	}

	/**
	 * Fired when a UIProperty value changes on this element.
	 * @param property The property that changed.
	 * @param oldValue The previous value.
	 * @param newValue The new value.
	 */
	protected <T> void onPropertyChanged(UIProperty<T> property, T oldValue, T newValue) {
		if (property.affectsLayout())
			invalidateMeasure();

		UIProperty.PropertyChangedCallback<T> callback = property.getChangedCallback();
		if (callback != null)
			callback.changed(this, oldValue, newValue);
	}

	public void clearProperty(UIProperty<?> property) {
		if (localProperties != null)
			localProperties.remove(property);
	}

	@SuppressWarnings("unchecked")
	public <T> void bindProperty(UIProperty<T> property, Setting<?> setting) {
		bindProperty(property, setting, BindingMode.OneWay);
	}

	@SuppressWarnings("unchecked")
	public <T> void bindProperty(UIProperty<T> property, Setting<?> setting, BindingMode mode) {
		Setting<T> typed = (Setting<T>) setting;
		setProperty(property, typed.getValue());
		Consumer<T> listener = v -> setProperty(property, v);
		typed.addOnUpdate(listener);

		if (propertyBindings == null)
			propertyBindings = new ArrayList<>();
		propertyBindings.add(new PropertyBinding<>(property, setting, listener, mode));
	}

	public <T> void unbindProperty(UIProperty<T> property) {
		if (propertyBindings == null)
			return;

		propertyBindings.removeIf(binding -> {
			if (binding.getProperty() == property) {
				binding.unbind();
				return true;
			}
			return false;
		});

		if (propertyBindings.isEmpty())
			propertyBindings = null;
	}

	/**
	 * Returns the parent of the element.
	 * @return Parent of the component as a ClickGuiTab.
	 */
	public UIElement getParent() {
		return parent;
	}


	public void setParent(UIElement parent) {
		this.parent = parent;
	}

	/**
	 * Invalidates the measurements (preferredSize) of the UI element.
	 */
	public void invalidateMeasure() {
		if (initialized) {
			measureDirty = true;

			if (parent != null) {
				parent.invalidateMeasure();
			} else {
				// Root element
				Float width = getProperty(UIElement.WidthProperty);
				Float height = getProperty(UIElement.HeightProperty);
				Float minWidth = getProperty(UIElement.MinWidthProperty);
				Float minHeight = getProperty(UIElement.MinHeightProperty);
				Float maxWidth = getProperty(UIElement.MaxWidthProperty);
				Float maxHeight = getProperty(UIElement.MaxHeightProperty);

				float w = width != null ? width : 0f;
				float h = height != null ? height : 0f;

				if (minWidth != null && w < minWidth) w = minWidth;
				if (minHeight != null && h < minHeight) h = minHeight;
				if (maxWidth != null && w > maxWidth) w = maxWidth;
				if (maxHeight != null && h > maxHeight) h = maxHeight;

				Size size = new Size(w, h);
				measureCore(size);
				float rw = Math.max(w, preferredSize.width());
				float rh = Math.max(h, preferredSize.height());
				arrange(new Rectangle(0f, 0f, rw, rh));
			}
		}
	}

	/**
	 * Invalidates the layout and actualSize of the UI element.
	 */
	public void invalidateArrange() {
		if (initialized) {
			arrange(actualSize);
		}
	}

	public final void measureCore(Size availableSize) {
		Boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		if (!isVisible || !initialized)
			return;

		// Skip if not dirty
		if (!measureDirty && lastMeasureWidth != null && lastMeasureHeight != null
				&& lastMeasureWidth.equals(availableSize.width())
				&& lastMeasureHeight.equals(availableSize.height())) {
			return;
		}

		// Reduce available size by padding.
		Thickness margin = getProperty(UIElement.MarginProperty);
		Thickness padding = getProperty(UIElement.PaddingProperty);
		Float width = getProperty(UIElement.WidthProperty);
		Float height = getProperty(UIElement.HeightProperty);
		Float minWidth = getProperty(UIElement.MinWidthProperty);
		Float minHeight = getProperty(UIElement.MinHeightProperty);
		Float maxWidth = getProperty(UIElement.MaxWidthProperty);
		Float maxHeight = getProperty(UIElement.MaxHeightProperty);
		
		Size contentAvailable = new Size(availableSize.width() - padding.horizontalSum(), availableSize.height() - padding.verticalSum());

		Size measured = measure(contentAvailable);
		float w = measured.width();
		float h = measured.height();
		
		// Add padding to preferred size.
		if (padding != null) {
			w += padding.horizontalSum();
			h += padding.verticalSum();
		}

		// Apply explicit width/height.
		if (width != null) 
			w = width;
		if (height != null) 
			h = height;

		// Apply min/max constraints
		if (minWidth != null && w < minWidth) w = minWidth;
		else if (maxWidth != null && w > maxWidth) w = maxWidth;

		if (minHeight != null && h < minHeight) h = minHeight;
		else if (maxHeight != null && h > maxHeight) h = maxHeight;

		// Apply margins
		if (margin != null) {
			w += margin.horizontalSum();
			h += margin.verticalSum();
		}

		preferredSize = new Size(w, h);
		lastMeasureWidth = availableSize.width();
		lastMeasureHeight = availableSize.height();
		measureDirty = false;
	}

	/**
	 * Measures the UI element accounting for all of the children.
	 * @param availableSize The total amount of space that the UI element has to fit in.
	 * @return The computed preferred size.
	 */
	public Size measure(Size availableSize) {
		return new Size(0f, 0f);
	}

	/**
	 * Arranges the UI element onto the screen.
	 * @param finalSize The final size available to the UI element as deemed by the parent.
	 */
	public void arrange(Rectangle finalSize) {
		if (initialized) {
			float fx = finalSize.x();
			float fy = finalSize.y();
			float fw = finalSize.width();
			float fh = finalSize.height();

			HorizontalAlignment horizontalAlignment = getProperty(UIElement.HorizontalAlignmentProperty);
			VerticalAlignment verticalAlignment = getProperty(UIElement.VerticalAlignmentProperty);
			Thickness margin = getProperty(UIElement.MarginProperty);
			Float width = getProperty(UIElement.WidthProperty);
			Float height = getProperty(UIElement.HeightProperty);
			
			if (margin != null) {
				fx += margin.left();
				fy += margin.top();
				fw -= margin.horizontalSum();
				fh -= margin.verticalSum();
			}

			// Apply horizontal alignment.
			float totalHorizontalMargin = margin != null ? margin.horizontalSum() : 0f;
			float available = finalSize.width() - totalHorizontalMargin;

			switch (horizontalAlignment) {
			case Stretch:
				if (width != null)
					fw = width;
				break;
			case Left:
				float desiredWidth = width != null ? width : preferredSize.width() - totalHorizontalMargin;
				desiredWidth = Math.min(desiredWidth, available);
				fw = desiredWidth;
				break;
			case Center:
				desiredWidth = width != null ? width : preferredSize.width() - totalHorizontalMargin;
				desiredWidth = Math.min(desiredWidth, available);
				fx += (available - desiredWidth) / 2f;
				fw = desiredWidth;
				break;
			case Right:
				desiredWidth = width != null ? width : preferredSize.width() - totalHorizontalMargin;
				desiredWidth = Math.min(desiredWidth, available);
				fx += available - desiredWidth;
				fw = desiredWidth;
				break;
			}

			// Apply vertical alignment.
			float totalVerticalMargin = margin != null ? margin.verticalSum() : 0f;
			float availableHeight = finalSize.height() - totalVerticalMargin;

			switch (verticalAlignment) {
			case Stretch:
				if (height != null)
					fh = height;
				break;
			case Top:
				float desiredHeight = height != null ? height : preferredSize.height() - totalVerticalMargin;
				desiredHeight = Math.min(desiredHeight, availableHeight);
				fh = desiredHeight;
				break;
			case Center:
				desiredHeight = height != null ? height : preferredSize.height() - totalVerticalMargin;
				desiredHeight = Math.min(desiredHeight, availableHeight);
				fy += (availableHeight - desiredHeight) / 2f;
				fh = desiredHeight;
				break;
			case Bottom:
				desiredHeight = height != null ? height : preferredSize.height() - totalVerticalMargin;
				desiredHeight = Math.min(desiredHeight, availableHeight);
				fy += availableHeight - desiredHeight;
				fh = desiredHeight;
				break;
			}

			setActualSize(new Rectangle(fx, fy, fw, fh));
		}
	}


	/**
	 * Fired when the element gains focus.
	 */
	protected void onGotFocus() {
	}

	/**
	 * Fired when the element loses focus.
	 */
	protected void onLostFocus() {
	}

	public void onMouseMove(MouseMoveEvent event) {
		boolean wasHovered = getProperty(UIElement.IsHoveredProperty);
		boolean isHitTestVisible = getProperty(UIElement.IsHitTestVisibleProperty);

		if (isHitTestVisible) {
			float mouseX = (float) event.getX();
			float mouseY = (float) event.getY();

			boolean state = actualSize.intersects(mouseX, mouseY);

			// TODO: Move this to a 'hovered' callback
			if (!event.isCancelled() && state) {
				event.cancel();
				String tooltip = getProperty(UIElement.ToolTipProperty);
				GuiManager.setTooltip(tooltip);
				CursorStyle cursor = getProperty(CursorProperty);
				if (cursor != null)
					GuiManager.setCursor(cursor);
			} else if (wasHovered && !state) {
				GuiManager.setTooltip(null);
				CursorStyle cursor = getProperty(CursorProperty);
				if (cursor != null)
					GuiManager.setCursor(CursorStyle.Default);
			}

			setProperty(UIElement.IsHoveredProperty, state);
		} else {
			setProperty(UIElement.IsHoveredProperty, false);
			if (wasHovered) {
				GuiManager.setTooltip(null);
			}
		}
	}

	public void onMouseClick(MouseClickEvent event) {
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		boolean isHitTestVisible = getProperty(UIElement.IsHitTestVisibleProperty);
		
		if (!event.isCancelled() && onClicked != null && isHitTestVisible && isVisible) {
			if (actualSize.intersects((float) event.mouseX, (float) event.mouseY)) {
				onClicked.accept(event);
			}
		}
	}

	public void onMouseScroll(MouseScrollEvent event) {
	}


	public Consumer<MouseClickEvent> getOnClicked() {
		return onClicked;
	}

	public void setOnClicked(Consumer<MouseClickEvent> onClicked) {
		this.onClicked = onClicked;
	}
}
