/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.entity;

public class PlayerUtils {
    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(squaredDistance(x1, y1, z1, x2, y2, z2));
    }

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double f = x1 - x2;
        double g = y1 - y2;
        double h = z1 - z2;
        return org.joml.Math.fma(f, f, org.joml.Math.fma(g, g, h * h));
    }
}
