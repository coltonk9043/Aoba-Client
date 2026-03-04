/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import java.util.ArrayList;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.ChunkListener;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkEvent {

	public static class Loaded extends AbstractEvent {
		private final LevelChunk chunk;

		public Loaded(LevelChunk chunk) {
			this.chunk = chunk;
		}

		public LevelChunk getChunk() {
			return chunk;
		}

		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : listeners) {
				ChunkListener chunkListener = (ChunkListener) listener;
				chunkListener.onChunkLoaded(this);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<ChunkListener> GetListenerClassType() {
			return ChunkListener.class;
		}
	}

	public static class Unloaded extends AbstractEvent {
		private final LevelChunk chunk;

		public Unloaded(LevelChunk chunk) {
			this.chunk = chunk;
		}

		public LevelChunk getChunk() {
			return chunk;
		}

		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : listeners) {
				ChunkListener chunkListener = (ChunkListener) listener;
				chunkListener.onChunkUnloaded(this);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<ChunkListener> GetListenerClassType() {
			return ChunkListener.class;
		}
	}
}
