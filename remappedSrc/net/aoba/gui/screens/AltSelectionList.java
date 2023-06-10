package net.aoba.gui.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.aoba.altmanager.Alt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;

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

	public void setSelected(@Nullable AltSelectionList.Entry entry) {
		super.setSelected(entry);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		AltSelectionList.Entry AltSelectionList$entry = this.getSelectedOrNull();
		return AltSelectionList$entry != null && AltSelectionList$entry.keyPressed(keyCode, scanCode, modifiers)
				|| super.keyPressed(keyCode, scanCode, modifiers);
	}

	public abstract static class Entry extends ElementListWidget.Entry<AltSelectionList.Entry> {
	}

	public class NormalEntry extends AltSelectionList.Entry {
		private final AltScreen owner;
		private final MinecraftClient mc;
		private final Alt alt;
		private long lastClickTime;
		private PlayerListEntry entry;

		protected NormalEntry(AltScreen ownerIn, Alt alt) {
			this.owner = ownerIn;
			this.alt = alt;
			this.mc = MinecraftClient.getInstance();

			try {
				String name = alt.getUsername();
				if (name.isEmpty()) {
					name = "Steve";
				}

				UUID uuid = Uuids.getUuidFromProfile(new GameProfile((UUID)null, name));
				entry = new PlayerListEntry(new GameProfile(uuid, name), false);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void getAltList() {
			this.owner.getAltList();
		}

		public Alt getAltData() {
			return this.alt;
		}

		@Override
		public void render(MatrixStack matrixStack, int index, int y, int x, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean hovered, float tickDelta) {
			
			String password = "";
			String description;
			
			// Generates string for the password of an alt.
			if (!this.alt.isCracked()) {
				for (int i = 0; i < this.alt.getPassword().toCharArray().length; i++) {
					password = password + "*";
				}
			} else {
				password = "None";
			}
			
			// Generates the description of an alt.
			if (this.alt.isCracked()) {
				description = "Cracked Account";
			} else {
				if (this.alt.isMicrosoft()) {
					description = "Microsoft Account";
				} else {
					description = "Mojang Account";
				}
			}
			
			// Draws the strings onto the screen.
			this.mc.textRenderer.drawWithShadow(matrixStack, "Username: " + this.alt.getEmail(), (float) (x + 32 + 3),
					(float) (y + 2), 16777215);
			this.mc.textRenderer.drawWithShadow(matrixStack, "Password: " + password, (float) (x + 32 + 3), (float) (y + 12),
					16777215);
			this.mc.textRenderer.drawWithShadow(matrixStack, description, (float) (x + 32 + 3), (float) (y + 22),
					this.alt.isCracked() ? 0xFF0000 : 0x00FF00);
			
			// Draws the respective player head.
			this.drawHead(matrixStack, x + 4, y + 4);
		}

		private void drawHead(MatrixStack matrixStack, int x, int y) {
			RenderSystem.setShaderTexture(0, entry.getSkinTexture());

			GL11.glEnable(GL11.GL_BLEND);
			RenderSystem.setShaderColor(1, 1, 1, 1);

			// Face
			int fw = 192;
			int fh = 192;
			float u = 24;
			float v = 24;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, 24, 24, fw, fh);

			// Hat
			fw = 192;
			fh = 192;
			u = 120;
			v = 24;
			DrawableHelper.drawTexture(matrixStack, x, y, u, v, 24, 24, fw, fh);

			GL11.glDisable(GL11.GL_BLEND);
		}

		@Override
		public List<? extends Element> children() {
			return Collections.emptyList();
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return Collections.emptyList();
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d0 = mouseX - (double) AltSelectionList.this.getRowLeft();

			if (d0 <= 32.0D) {
				if (d0 < 32.0D && d0 > 16.0D) {
					this.owner.setSelected(this);
					this.owner.loginToSelected();
					return true;
				}
			}
			this.owner.setSelected(this);
			if (Util.getMeasuringTimeMs() - this.lastClickTime < 250L) {
				this.owner.loginToSelected();
			}
			this.lastClickTime = Util.getMeasuringTimeMs();
			return false;
		}
	}
}
