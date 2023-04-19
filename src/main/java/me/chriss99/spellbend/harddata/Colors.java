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

    public static Color getRandomOrange3or4() {
        return (Color) MathUtil.randomEntry(new Color[]{orange3, orange4});
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

    public static final Color blue1 = Color.fromRGB(0,255,255);
    public static final Color blue2 = Color.fromRGB(0,204,204);
    public static final Color blue3 = Color.fromRGB(0,153,153);
    public static final Color blue4 = Color.fromRGB(0,102,102);
    public static final Color blue5 = Color.fromRGB(0,51,51);
    public static final Color gold1 = Color.fromRGB(255,215,0);
    public static final Color gold2 = Color.fromRGB(220,180,0);

    public static Color getRandomBlue1to3() {
        return (Color) MathUtil.randomEntry(new Color[]{blue1, blue2, blue3});
    }

    public static Color getRandomBlue3to5() {
        return (Color) MathUtil.randomEntry(new Color[]{blue3, blue4, blue5});
    }

    public static Color getRandomBlue2to5() {
        return (Color) MathUtil.randomEntry(new Color[]{blue2, blue3, blue4, blue5});
    }

    public static Color getRandomBlue4to5() {
        return (Color) MathUtil.randomEntry(new Color[]{blue4, blue5});
    }
}
