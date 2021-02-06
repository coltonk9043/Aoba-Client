package aoba.main.misc;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.item.*;

public class Utils {

	public static boolean isThrowable(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof BowItem || item instanceof SnowballItem || item instanceof EggItem || item instanceof EnderPearlItem || item instanceof SplashPotionItem || item instanceof LingeringPotionItem || item instanceof FishingRodItem;
    }
	
	public static int convertRGBToHex(int r, int g, int b) {
		String strr = StringUtils.leftPad(Integer.toHexString(r), 2, '0');
		String strg = StringUtils.leftPad(Integer.toHexString(g), 2, '0');
		String strb = StringUtils.leftPad(Integer.toHexString(b), 2, '0');
		String string = strr + strg + strb;
	    return Integer.parseInt(string,16);
	}
}
