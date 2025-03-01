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
import net.aoba.event.listeners.PlaySoundListener;
import net.minecraft.client.sound.SoundInstance;

public class PlaySoundEvent extends AbstractEvent {
	private final SoundInstance soundInstance;

	public PlaySoundEvent(SoundInstance soundInstance) {
		this.soundInstance = soundInstance;
	}

	public SoundInstance getSoundInstance() {
		return soundInstance;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			PlaySoundListener playSoundListener = (PlaySoundListener) listener;
			playSoundListener.onPlaySound(this);
		}
	}

	@Override
	public Class<PlaySoundListener> GetListenerClassType() {
		return PlaySoundListener.class;
	}
}
