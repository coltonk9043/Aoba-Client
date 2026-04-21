/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextUtils {
    public static String idToName(String ID) {
        return Arrays.stream(ID.split("_")).map(TextUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static String capitalize(String str) {
        if (str.length() > 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }
    
	public static float ptToFontSize(float fontSizePt) {
		// 1 pt = 96/72 DIPs
		// MC font defaults to 8 px height
		// 8 / (96/72 = 6
		return fontSizePt / 6;
	}
}
