package aoba.main.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import com.google.common.base.Splitter;
import aoba.main.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.nbt.CompoundNBT;

public class Settings {
	private File aobaOptions;
	private Minecraft mc;

	public Settings() {
		mc = Minecraft.getInstance();
		aobaOptions = new File(mc.gameDir, "aoba_options.txt");
		readSettings();
	}

	public void saveSettings() {
		PrintWriter printwriter = null;
		try {
			printwriter = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(aobaOptions), StandardCharsets.UTF_8));

			printwriter.println("x:" + mc.aoba.hud.x);
			printwriter.println("y:" + mc.aoba.hud.y);
			printwriter.println("width:" + mc.aoba.hud.width);
			printwriter.println("height:" + mc.aoba.hud.height);
			printwriter.println("color:" + mc.aoba.hm.getColor());

			for (Module module : mc.aoba.mm.modules) {
				printwriter.println(
						"key_" + module.getBind().getKeyDescription() + ":" + module.getBind().getTranslationKey());
			}

		} catch (Exception exception) {
			Minecraft.LOGGER.error("[Aoba] Failed to save settings", (Throwable) exception);
		} finally {
			IOUtils.closeQuietly((Writer) printwriter);
		}
	}

	public void readSettings() {
		final Splitter COLON_SPLITTER = Splitter.on(':');
		try {
			if (!this.aobaOptions.exists()) {
				return;
			}

			List<String> list = IOUtils.readLines(new FileInputStream(this.aobaOptions), StandardCharsets.UTF_8);
			CompoundNBT nbttagcompound = new CompoundNBT();

			for (String s : list) {
				try {
					Iterator<String> iterator = COLON_SPLITTER.limit(2).split(s).iterator();
					nbttagcompound.putString(iterator.next(), iterator.next());
				} catch (Exception var10) {
					Minecraft.LOGGER.warn("Skipping bad option: {}", (Object) s);
				}
			}

			for (String s1 : nbttagcompound.keySet()) {
				String s2 = nbttagcompound.getString(s1);
				if (s2 != null) {
					switch (s1) {
					case ("x"):
						mc.aoba.hud.x = Integer.valueOf(s2);
						break;
					case ("y"):
						mc.aoba.hud.y = Integer.valueOf(s2);
						break;
					case ("width"):
						mc.aoba.hud.width = Integer.valueOf(s2);
						break;
					case ("height"):
						mc.aoba.hud.height = Integer.valueOf(s2);
						break;
					case ("color"):
						mc.aoba.hm.setColor(Integer.valueOf(s2));
						break;
					default:
						try {
							for (Module module : mc.aoba.mm.modules) {
								if (s1.equals("key_" + module.getBind().getKeyDescription())) {
									module.getBind().bind(InputMappings.getInputByName(s2));
								}
							}
						} catch (Exception e) {
							Minecraft.LOGGER.warn("Skipping bad option: {}:{}", s1, s2);
						}
						break;
					}
				}
			}
			KeyBinding.resetKeyBindingArrayAndHash();
		} catch (Exception exception) {
			Minecraft.LOGGER.error("[Aoba] Failed to load settings", (Throwable) exception);
		}
	}
}
