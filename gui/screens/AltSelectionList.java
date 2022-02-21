package net.aoba.gui.screens;

import java.util.ArrayList;
import java.util.List;
import net.aoba.altmanager.Alt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;

public class AltSelectionList extends ElementListWidget<AltSelectionList.Entry> {
	private final AltScreen owner;
	private final List<AltSelectionList.NormalEntry> altList = new ArrayList<AltSelectionList.NormalEntry>();

	public AltSelectionList(AltScreen ownerIn, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.owner = ownerIn;
	}

	public void updateAlts() {
		this.clearEntries();
		for (Alt alt : this.owner.getAltList()) {
			AltSelectionList.NormalEntry entry = new AltSelectionList.NormalEntry(this.owner, alt);
			altList.add(entry);
		}
		this.setList();
	}

	private void setList() {
		this.altList.forEach(this::addEntry);
	}

	public abstract static class Entry extends ElementListWidget.Entry<AltSelectionList.Entry> {
	}

	public class NormalEntry extends AltSelectionList.Entry {
		private final AltScreen owner;
		private final MinecraftClient mc;
		private final Alt alt;
		private Resource icon;
		private long lastClickTime;

		protected NormalEntry(AltScreen ownerIn, Alt alt) {
			this.owner = ownerIn;
			this.alt = alt;
			this.mc = MinecraftClient.getInstance();
			this.icon = null;
		}

		public void getAltList() {
			this.owner.getAltList();
		}

		public Alt getAltData() {
			return this.alt;
		}

		@Override
		public List<? extends Element> children() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void render(MatrixStack matrixStack, int index, int y, int x, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.mc.textRenderer.drawWithShadow(matrixStack, "Username: " + this.alt.getEmail(), (float) (x + 32 + 3),
					(float) (y + 2), 16777215);
			String s = "";
			for (char i : this.alt.getPassword().toCharArray()) {
				s = s + "*";
			}
			this.mc.textRenderer.drawWithShadow(matrixStack, "Password: " + s, (float) (x + 32 + 3), (float) (y + 12),
					16777215);
			this.mc.textRenderer.drawWithShadow(matrixStack,
					this.alt.isCracked() ? "Cracked Account" : "Premium Account", (float) (x + 32 + 3),
					(float) (y + 22), this.alt.isCracked() ? 0xFF0000 : 0x00FF00);

		}
	}
}
