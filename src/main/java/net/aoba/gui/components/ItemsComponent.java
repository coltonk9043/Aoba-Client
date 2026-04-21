/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.aoba.gui.UIElement;
import net.aoba.utils.types.IObservableCollection;

public class ItemsComponent<T> extends Component {
	private Collection<T> itemsSource;
	private Function<T, UIElement> itemGenerator;

	private final Consumer<IObservableCollection<T>> observableListener = this::onItemsSourceChanged;
	
	private final PanelComponent parentComponent;

	public ItemsComponent(Collection<T> itemsSource) {

		this.itemsSource = itemsSource;
		StackPanelComponent newParent = new StackPanelComponent();
		newParent.setSpacing(8f);
		parentComponent = newParent;
		setContent(parentComponent);
		subscribeToItemsSource();
	}

	public ItemsComponent(Collection<T> itemsSource, Function<T, UIElement> itemGenerator) {
		this.itemGenerator = itemGenerator;
		this.itemsSource = itemsSource;

		StackPanelComponent newParent = new StackPanelComponent();
		newParent.setSpacing(8f);
		parentComponent = newParent;

		setContent(parentComponent);
		subscribeToItemsSource();
	}
	
	public ItemsComponent(Collection<T> itemsSource, Supplier<PanelComponent> parentGenerator, Function<T, UIElement> itemGenerator) {
		this.itemGenerator = itemGenerator;
		this.itemsSource = itemsSource;
		
		parentComponent = parentGenerator.get();

		setContent(parentComponent);
		subscribeToItemsSource();
	}

	@Override
	protected void onInitialized() {
		generateItems();
	}

	@SuppressWarnings("unchecked")
	private void subscribeToItemsSource() {
		if (itemsSource instanceof IObservableCollection) {
			((IObservableCollection<T>) itemsSource).addListener(observableListener);
		}
	}

	@SuppressWarnings("unchecked")
	private void unsubscribeFromItemsSource() {
		if (itemsSource instanceof IObservableCollection) {
			((IObservableCollection<T>) itemsSource).removeListener(observableListener);
		}
	}

	private void onItemsSourceChanged(IObservableCollection<T> list) {
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
				itemsSource.stream().map(s -> itemGenerator.apply(s)).forEach(s -> {
					parentComponent.addChild(s);
				});
			}
		}
		parentComponent.invalidateMeasure();
	}

	public Collection<T> getItemsSource() {
		return itemsSource;
	}

	public void setItemsSource(List<T> itemsSource) {
		unsubscribeFromItemsSource();
		this.itemsSource = itemsSource;
		subscribeToItemsSource();
		generateItems();
		invalidateMeasure();
	}

	@Override
	public void dispose() {
		unsubscribeFromItemsSource();
		super.dispose();
	}
}