/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.List;
import java.util.function.Function;

import net.aoba.gui.Margin;
import net.aoba.gui.UIElement;

// TODO: This works OKAY if we have a reference to this specific component when the items source is modified externally,
// but how do we notify ItemsComponent when the object modifying items source does NOT have a reference to ItemsComponent?
// .NET has ObservableCollection, but that'd be a pain to implement client-wide.
public class ItemsComponent<T> extends Component {
	private List<T> itemsSource;
	private Function<T, UIElement> itemGenerator;

	private final Component parentComponent;

	public ItemsComponent(List<T> itemsSource) {
        setMargin(new Margin(2f, null, 2f, null));
		this.itemsSource = itemsSource;
		parentComponent = new StackPanelComponent();

		addChild(parentComponent);
	}

	public ItemsComponent(List<T> itemsSource, Function<T, UIElement> itemGenerator) {
        this.itemGenerator = itemGenerator;
		setMargin(new Margin(2f, null, 2f, null));
		this.itemsSource = itemsSource;
		parentComponent = new StackPanelComponent();

		addChild(parentComponent);
	}

	@Override
	protected void onInitialized() {
		generateItems();
	}

	private void generateItems() {
		parentComponent.clearChildren();
		if (itemsSource != null) {
			if (itemGenerator == null) {
				itemsSource.stream().map(s -> new StringComponent(s.toString())).forEach(s -> {
					parentComponent.addChild(parentComponent);
				});
			} else {
				itemsSource.stream().map(s -> itemGenerator.apply(s)).forEach(s -> {
					parentComponent.addChild(s);
				});
			}
		}
		parentComponent.invalidateMeasure();
	}

	public List<T> getItemsSource() {
		return itemsSource;
	}

	public void setItemsSource(List<T> itemsSource) {
		this.itemsSource = itemsSource;
		parentComponent.clearChildren();
		generateItems();
	}
}