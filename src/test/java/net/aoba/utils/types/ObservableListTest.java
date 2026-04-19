/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class ObservableListTest {

    @Test
    @DisplayName("Default constructor creates an empty list")
    void defaultConstructor_empty() {
        ObservableList<String> list = new ObservableList<>();
        assertThat(list).isEmpty();
        assertThat(list.size()).isZero();
    }

    @Test
    @DisplayName("Copy constructor takes a defensive copy of the initial list")
    void copyConstructor_defensiveCopy() {
        List<String> source = new java.util.ArrayList<>(List.of("a", "b"));
        ObservableList<String> list = new ObservableList<>(source);
        source.add("c");
        assertThat(list).containsExactly("a", "b");
    }

    @Test
    @DisplayName("add(T) notifies listeners once per successful addition")
    void add_notifiesListeners() {
        AtomicInteger calls = new AtomicInteger();
        ObservableList<String> list = new ObservableList<>();
        list.addListener(observed -> calls.incrementAndGet());

        list.add("hello");
        list.add("world");

        assertThat(calls.get()).isEqualTo(2);
        assertThat(list).containsExactly("hello", "world");
    }

    @Test
    @DisplayName("remove(Object) only notifies when an element was actually removed")
    void remove_notifiesOnlyWhenChanged() {
        AtomicInteger calls = new AtomicInteger();
        ObservableList<String> list = new ObservableList<>(List.of("a"));
        list.addListener(observed -> calls.incrementAndGet());

        boolean removedMissing = list.remove("b");
        assertThat(removedMissing).isFalse();
        assertThat(calls.get()).isZero();

        boolean removedPresent = list.remove("a");
        assertThat(removedPresent).isTrue();
        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("clear() notifies listeners only when the list was non-empty")
    void clear_notifiesOnlyWhenNonEmpty() {
        AtomicInteger calls = new AtomicInteger();
        ObservableList<String> list = new ObservableList<>();
        list.addListener(observed -> calls.incrementAndGet());

        list.clear();
        assertThat(calls.get()).isZero();

        list.add("a");
        calls.set(0);
        list.clear();
        assertThat(list).isEmpty();
        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("set(index, element) only notifies when the new value differs")
    void set_notifiesOnlyWhenChanged() {
        AtomicInteger calls = new AtomicInteger();
        ObservableList<String> list = new ObservableList<>(List.of("a", "b"));
        list.addListener(observed -> calls.incrementAndGet());

        list.set(0, "a");
        assertThat(calls.get()).isZero();

        list.set(0, "z");
        assertThat(calls.get()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("z");
    }

    @Test
    @DisplayName("removeListener detaches the listener")
    void removeListener_detaches() {
        AtomicInteger calls = new AtomicInteger();
        ObservableList<String> list = new ObservableList<>();
        Consumer<IObservableList<String>> listener = observed -> calls.incrementAndGet();
        list.addListener(listener);
        list.removeListener(listener);

        list.add("a");
        assertThat(calls.get()).isZero();
    }

    @Test
    @DisplayName("addAll notifies listeners when the list grows")
    void addAll_notifiesOnGrowth() {
        AtomicInteger calls = new AtomicInteger();
        ObservableList<String> list = new ObservableList<>();
        list.addListener(observed -> calls.incrementAndGet());

        list.addAll(List.of("a", "b", "c"));
        assertThat(calls.get()).isEqualTo(1);
        assertThat(list).containsExactly("a", "b", "c");

        list.addAll(List.of());
        assertThat(calls.get()).isEqualTo(1);
    }
}
