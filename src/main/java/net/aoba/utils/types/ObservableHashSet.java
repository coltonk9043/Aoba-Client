package net.aoba.utils.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ObservableHashSet <T> implements Set<T>, IObservableCollection<T> {
	private final HashSet<T> backing;
	private final List<Consumer<IObservableCollection<T>>> listeners = new ArrayList<>();

	public ObservableHashSet() {
		this.backing = new HashSet<>();
	}

	public ObservableHashSet(List<T> initial) {
		this.backing = new HashSet<>(initial);
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
}
