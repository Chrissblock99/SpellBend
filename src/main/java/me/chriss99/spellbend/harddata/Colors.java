package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.Color;

public class Colors {
    public static final Color orange1 = Color.fromRGB(255,255,0);
    public static final Color orange2 = Color.fromRGB(255,165,0);
    public static final Color orange3 = Color.fromRGB(230,149,0);
    public static final Color orange4 = Color.fromRGB(204,132,0);
    public static final Color orange5 = Color.fromRGB(255,192,0);
    public static final Color orange6 = Color.fromRGB(255,128,0);

    public static Color getRandomOrange1or2() {
        return (Color) MathUtil.randomEntry(new Color[]{orange1, orange2});
    }

    public static Color getRandomOrange5or6() {
        return (Color) MathUtil.randomEntry(new Color[]{orange5, orange6});
    }

    public static final Color yellow1 = Color.fromRGB(255,255,0);
    public static final Color yellow2 = Color.fromRGB(173,255,47);
    public static final Color yellow3 = Color.fromRGB(130,191,35);

    public static Color getRandomYellow1or2() {
        return (Color) MathUtil.randomEntry(new Color[]{yellow1, yellow2});
    }
}
