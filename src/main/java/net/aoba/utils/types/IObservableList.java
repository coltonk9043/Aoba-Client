/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.types;

import java.util.function.Consumer;

public interface IObservableList<T> {
	void addListener(Consumer<IObservableList<T>> listener);
	void removeListener(Consumer<IObservableList<T>> listener);
}
