/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.aoba.gui.UIElement;
import net.aoba.utils.types.IObservableList;

public class ItemsComponent<T> extends Component {
	private List<T> itemsSource;
	private Function<T, UIElement> itemGenerator;
	private final Consumer<IObservableList<T>> observableListener = this::onItemsSourceChanged;

	private final Component parentComponent;

	public ItemsComponent(List<T> itemsSource) {
		this.itemsSource = itemsSource;
		
		StackPanelComponent newParent = new StackPanelComponent();
		newParent.setSpacing(4f);
		parentComponent = newParent;
		addChild(parentComponent);
		subscribeToItemsSource();
	}

	public ItemsComponent(List<T> itemsSource, Function<T, UIElement> itemGenerator) {
		this.itemGenerator = itemGenerator;
		this.itemsSource = itemsSource;
		StackPanelComponent newParent = new StackPanelComponent();
		newParent.setSpacing(4f);
		parentComponent = newParent;

		addChild(parentComponent);
		subscribeToItemsSource();
	}

	@Override
	protected void onInitialized() {
		generateItems();
	}

	@SuppressWarnings("unchecked")
	private void subscribeToItemsSource() {
		if (itemsSource instanceof IObservableList) {
			((IObservableList<T>) itemsSource).addListener(observableListener);
		}
	}

	@SuppressWarnings("unchecked")
	private void unsubscribeFromItemsSource() {
		if (itemsSource instanceof IObservableList) {
			((IObservableList<T>) itemsSource).removeListener(observableListener);
		}
	}

	private void onItemsSourceChanged(IObservableList<T> list) {
		generateItems();
		invalidateMeasure();
	}

	private void generateItems() {
		parentComponent.clearChildren();
		if (itemsSource != null) {
			if (itemGenerator == null) {
				itemsSource.stream().map(s -> new StringComponent(s.toString())).forEach(s -> {
					parentComponent.addChild(s);
				});
			} else {
				System.out.println("Generating " + itemsSource.size() + " items");
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
		unsubscribeFromItemsSource();
		this.itemsSource = itemsSource;
		subscribeToItemsSource();
		generateItems();
		invalidateMeasure();
	}
}