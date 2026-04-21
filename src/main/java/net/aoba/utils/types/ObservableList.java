/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

public class ObservableList<T> implements List<T>, IObservableCollection<T> {
	private final List<T> backing;
	private final List<Consumer<IObservableCollection<T>>> listeners = new ArrayList<>();

	public ObservableList() {
		this.backing = new ArrayList<>();
	}

	public ObservableList(List<T> initial) {
		this.backing = new ArrayList<>(initial);
	}

	@Override
	public void addListener(Consumer<IObservableCollection<T>> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(Consumer<IObservableCollection<T>> listener) {
		listeners.remove(listener);
	}

	private void notifyListeners() {
		if (listeners.isEmpty()) return;
		for (Consumer<IObservableCollection<T>> listener : listeners) {
			listener.accept(this);
		}
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public boolean isEmpty() {
		return backing.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return backing.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return backing.iterator();
	}

	@Override
	public Object[] toArray() {
		return backing.toArray();
	}

	@Override
	public <U> U[] toArray(U[] a) {
		return backing.toArray(a);
	}

	@Override
	public boolean add(T t) {
		boolean result = backing.add(t);
		if (result) notifyListeners();
		return result;
	}

	@Override
	public boolean remove(Object o) {
		boolean result = backing.remove(o);
		if (result) notifyListeners();
		return result;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backing.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean result = backing.addAll(c);
		if (result) notifyListeners();
		return result;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		boolean result = backing.addAll(index, c);
		if (result) notifyListeners();
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = backing.removeAll(c);
		if (result) notifyListeners();
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = backing.retainAll(c);
		if (result) notifyListeners();
		return result;
	}

	@Override
	public void clear() {
		if (!backing.isEmpty()) {
			backing.clear();
			notifyListeners();
		}
	}

	@Override
	public T get(int index) {
		return backing.get(index);
	}

	@Override
	public T set(int index, T element) {
		T old = backing.set(index, element);
		if (old == null ? element != null : !old.equals(element)) {
			notifyListeners();
		}
		return old;
	}

	@Override
	public void add(int index, T element) {
		backing.add(index, element);
		notifyListeners();
	}

	@Override
	public T remove(int index) {
		T old = backing.remove(index);
		notifyListeners();
		return old;
	}

	@Override
	public int indexOf(Object o) {
		return backing.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return backing.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return backing.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return backing.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return backing.subList(fromIndex, toIndex);
	}
}
