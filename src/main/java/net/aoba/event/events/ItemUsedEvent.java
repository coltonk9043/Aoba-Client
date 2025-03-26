package net.aoba.event.events;

import java.util.ArrayList;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.ItemUsedListener;
import net.minecraft.item.ItemStack;

public class ItemUsedEvent {
	public static class Pre extends AbstractEvent {
		private final ItemStack itemStack;

		public Pre(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : listeners) {
				ItemUsedListener itemUsedEvent = (ItemUsedListener) listener;
				itemUsedEvent.onItemUsed(this);
			}
		}

		@Override
		public Class<ItemUsedListener> GetListenerClassType() {
			return ItemUsedListener.class;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}
	}

	public static class Post extends AbstractEvent {
		private final ItemStack itemStack;

		public Post(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : listeners) {
				ItemUsedListener itemUsedEvent = (ItemUsedListener) listener;
				itemUsedEvent.onItemUsed(this);
			}
		}

		@Override
		public Class<ItemUsedListener> GetListenerClassType() {
			return ItemUsedListener.class;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}
	}
}
