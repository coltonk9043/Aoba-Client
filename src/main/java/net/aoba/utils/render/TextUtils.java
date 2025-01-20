/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.render;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextUtils {
    public static String IDToName(String ID) {
        return Arrays.stream(ID.split("_")).map(TextUtils::Capitalize).collect(Collectors.joining(" "));
    }

    public static String Capitalize(String str) {
        if (str.length() > 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }
}
