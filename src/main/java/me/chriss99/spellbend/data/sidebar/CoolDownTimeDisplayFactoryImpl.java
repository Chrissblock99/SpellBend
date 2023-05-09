package me.chriss99.spellbend.data.sidebar;

import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.util.TextUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import org.jetbrains.annotations.NotNull;

public class CoolDownTimeDisplayFactoryImpl implements AbstractCoolDownTimeDisplayFactory{

    @Override
    public @NotNull CoolDownTimeDisplay createCoolDownTimeDisplay(@NotNull CoolDownEntry coolDown) {
        String name = "§7" + TextUtil.standardCapitalize(coolDown.getSpellType()) + " - " + coolDown.getCoolDownStage().toString().toLowerCase();

        StringBuilder coolDownDisplay = null; //only needed so compiler doesn't complain
        switch (coolDown.getCoolDownStage()) {
            case WINDUP -> coolDownDisplay =
                    buildTimeDisplay(coolDown, 'e', '8', true, 'b');
            case ACTIVE -> coolDownDisplay =
                    buildTimeDisplay(coolDown, 'e', '8', false, 'a');
            case PASSIVE -> coolDownDisplay =
                    buildTimeDisplay(coolDown, 'b', 'a', false, '8');
            case COOLDOWN -> coolDownDisplay =
                    buildTimeDisplay(coolDown, 'b', 'b', true, '8');
        }

        return new CoolDownTimeDisplay(name, coolDownDisplay.toString());
    }

    private static @NotNull StringBuilder buildTimeDisplay(@NotNull CoolDownEntry coolDownEntry, char numColor, char barColor1, boolean backwards, char barColor2) {
        double forwards = (coolDownEntry.getRemainingCoolDownStageTimeInS()/coolDownEntry.getStageTimeInS())*10;
        int filled = (int) Math.round(backwards ? 10-forwards : forwards);

        StringBuilder timeDisplay = new StringBuilder("§").append(numColor)
                .append(MathUtil.roundToNDigits(coolDownEntry.getRemainingCoolDownStageTimeInS(), 1))
                .append("s §").append(barColor1).append("▌▌▌▌▌▌▌▌▌▌");
        return timeDisplay.insert(timeDisplay.length()-10 + filled, "§" + barColor2);
    }
}
