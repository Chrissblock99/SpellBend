package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * This data structure is not mutable, except for the coolDownStage and timeInS array!
 */
public class CoolDownEntry {
    private final String spellType;
    private final Date startDate;
    private final float[] timeInS;
    private Enums.CoolDownStage coolDownStage;

    /**
     * Creates a CoolDownEntry
     *
     * @throws IllegalArgumentException If the timeInS array length is not 4
     *
     * @param spellType The spellType it is of
     * @param startDate The Date to start at
     * @param timeInS The time it should last in seconds for every CoolDownStage <br>
     *                0: WINDUP, 1: ACTIVE, 2: PASSIVE, 3: COOLDOWN
     * @param coolDownStage The coolDownStage to start in
     */
    public CoolDownEntry(@NotNull String spellType, @NotNull Date startDate, float[] timeInS, @NotNull Enums.CoolDownStage coolDownStage) {
        if (timeInS.length != 4)
            throw new IllegalArgumentException("Not more or less than four coolDownStages can be specified!");

        this.spellType = spellType;
        this.startDate = startDate;
        this.timeInS = timeInS;
        this.coolDownStage = coolDownStage;
    }

    /**
     * Creates a CoolDownEntry using the current time as startDate
     *
     * @throws IllegalArgumentException If the timeInS array length is not 4
     *
     * @param spellType The spellType it is of
     * @param timeInS The time it should last in seconds for every CoolDownStage <br>
     *                0: WINDUP, 1: ACTIVE, 2: PASSIVE, 3: COOLDOWN
     * @param coolDownStage The coolDownStage to start in
     */
    public CoolDownEntry(@NotNull String spellType, float[] timeInS, @NotNull Enums.CoolDownStage coolDownStage) {
        if (timeInS.length != 4)
            throw new IllegalArgumentException("Not more or less than four coolDownStages can be specified!");

        this.spellType = spellType;
        this.startDate = new Date();
        this.timeInS = timeInS;
        this.coolDownStage = coolDownStage;
    }

    /**
     * Updates the coolDownStage to fit with coolDownTime
     */
    public void updateCoolDownStage() {
        int i = 0;
        float timeSinceStartInS = (new Date().getTime() - startDate.getTime()) / 1000f;
        while (i<4 && !(getTimeToStageInS(i + 1) > timeSinceStartInS))
            i++;
        coolDownStage = Enums.CoolDownStage.values()[i];
    }

    /**
     * Skips the current CoolDownStage and starts the next <br>
     * editing the timeInS array in the process <br>
     * <br>
     * <b>This does NOT update the coolDownStage so make sure to update before checking whatever makes you skip</b> <br>
     * It doesn't update because it could be that the code checks in a millisecond just before it gets to the next stage <br>
     * and if the code then updates the coolDownStage and THEN skips, it will skip a stage that wasn't intended to be skipped.
     *
     * @return How much time was skipped
     */
    public float skipCurrentStage() {
        float timeSinceStartInS = (new Date().getTime() - startDate.getTime()) / 1000f;

        int currentStageIndex = Maps.coolDownStageToIndexMap.get(coolDownStage);
        //                  time to end of this stage          minus        time Since start       equals the remaining time of this Stage
        float timeToSkip = getTimeToStageInS(currentStageIndex+1) - (new Date().getTime() - startDate.getTime()) / 1000f;
        timeInS[currentStageIndex] -= timeToSkip;

        //we just assume the stage is updated WHICH CAN LEAD TO PROBLEMS, but I haven't found a fix yet
        int newIndex = Maps.coolDownStageToIndexMap.get(coolDownStage) + 1;
        if (newIndex != 4)
            coolDownStage = Enums.CoolDownStage.values()[newIndex];

        return timeToSkip;
    }

    /**
     * Sets the coolDownStage to the one referred to by the index and starts it <br>
     * editing the timeInS array im the process
     *
     * @throws IllegalArgumentException If the given index is not between or equal to 0 and 3
     *
     * @param index The index of the coolDownStage
     * @return How much time was skipped
     */
    public float skipToStage(int index) {
        if (index < 0 || index > 3)
            throw new IllegalArgumentException("There are only four coolDownStages!");

        return skipToStage(Enums.CoolDownStage.values()[index]);
    }

    /**
     * Sets the coolDownStage and starts it <br>
     * editing the timeInS array in the process <br>
     *
     * @throws IllegalArgumentException If the stage has already been passed
     *
     * @param coolDownStage The coolDownStage
     * @return How much time was skipped
     */
    public float skipToStage(@NotNull Enums.CoolDownStage coolDownStage) {
        updateCoolDownStage();
        if (Maps.coolDownStageToIndexMap.get(coolDownStage)<Maps.coolDownStageToIndexMap.get(this.coolDownStage) || coolDownStage.equals(this.coolDownStage))
            throw new IllegalArgumentException("The CoolDownStage to skip to cannot be older than or the same as the current one!");

        float timeSkipped = skipCurrentStage();
        int stageToSkipToIndex = Maps.coolDownStageToIndexMap.get(coolDownStage);
        for(int i = Maps.coolDownStageToIndexMap.get(this.coolDownStage)+1;i<stageToSkipToIndex;i++) {
            timeSkipped += timeInS[i];
            timeInS[i] = 0;
        }
        this.coolDownStage = coolDownStage;

        return timeSkipped;
    }

    /**
     * Skips to the given Stage and adds all the time skipped to it
     *
     * @param index The CoolDownStage to transform to
     * @return The skipped time
     */
    public float transformToStage(int index) {
        if (index < 0 || index > 3)
            throw new IllegalArgumentException("There are only four coolDownStages!");

        return transformToStage(Enums.CoolDownStage.values()[index]);
    }

    /**
     * Skips to the given Stage and adds all the time skipped to it
     *
     * @param coolDownStage The CoolDownStage to transform to
     * @return The skipped time
     */
    public float transformToStage(@NotNull Enums.CoolDownStage coolDownStage) {
        float transformedTime = skipToStage(coolDownStage);
        timeInS[Maps.coolDownStageToIndexMap.get(coolDownStage)] += transformedTime;
        return  transformedTime;
    }

    /**
     * Works independently of the stage being updated
     *
     * @return The remaining time for the current CoolDownStage and all following
     */
    public float getRemainingCoolDownTimeInS() {
        return getTimeInS() - (new Date().getTime() - startDate.getTime()) / 1000f;
    }

    /**
     * @return The remaining time for the current CoolDownStage
     */
    public float getRemainingCoolDownStageTimeInS() {
        updateCoolDownStage();
        return getStageTimeInS() - (new Date().getTime() - getStageStartDate().getTime()) / 1000f;
    }

    /**
     * @return The startDate of the coolDownEntry
     */
    public @NotNull Date getStartDate() {
        return startDate;
    }

    /**
     * @throws IllegalArgumentException If the given index is not between or equal to 0 and 3
     *
     * @param index The index in the timeInS array to get the StartDate of <br>
     *              0: WINDUP, 1: ACTIVE, 2: PASSIVE, 3: COOLDOWN
     * @return The startDate of the CoolDownStage at the index
     */
    public @NotNull Date getStageStartDate(int index) {
        if (index < 0 || index > 3)
            throw new IllegalArgumentException("There are only four coolDownStages!");

        return new Date(startDate.getTime() + (long) getTimeToStageInS(index)*1000);
    }

    /**
     * @param coolDownStage The coolDownStage to get the StartDate of
     * @return The startDate of the CoolDownStage
     */
    public @NotNull Date getStageStartDate(@NotNull Enums.CoolDownStage coolDownStage) {
        return getStageStartDate(Maps.coolDownStageToIndexMap.get(coolDownStage));
    }

    /**
     * @return The startDate of the current CoolDownStage
     */
    public @NotNull Date getStageStartDate() {
        updateCoolDownStage();
        return getStageStartDate(Maps.coolDownStageToIndexMap.get(coolDownStage));
    }

    /**
     * @return The time the entire coolDownEntry takes
     */
    public float getTimeInS() {
        float time = 0;
        for (double d : timeInS)
            time += d;
        return time;
    }

    /**
     * Independent of the stage being updated.
     *
     * @throws IllegalArgumentException If the given index is not between or equal to 0 and 4
     *
     * @param index The index in the timeInS array to get the time it takes to reach from start of <br>
     *              0: WINDUP (always 0), 1: ACTIVE, 2: PASSIVE, 3: COOLDOWN, 4: all added
     *
     * @return The time the coolDown takes to reach the specified index in the TimeInS array
     */
    public float getTimeToStageInS(int index) {
        if (index < 0 || index > 4)
            throw new IllegalArgumentException("There are only four coolDownStages!");

        float time = 0;
        for (int i = 0;i<index;i++)
            time += timeInS[i];
        return time;
    }

    /**
     * @return The time the coolDown takes to reach the named CoolDownStage
     */
    public float getTimeToStageInS(@NotNull Enums.CoolDownStage coolDownStage) {
        return getTimeToStageInS(Maps.coolDownStageToIndexMap.get(coolDownStage));
    }

    /**
     * @return The time the coolDown takes to reach the current CoolDownStage
     */
    public float getTimeToStageInS() {
        updateCoolDownStage();
        return getTimeToStageInS(Maps.coolDownStageToIndexMap.get(coolDownStage));
    }

    /**
     * @return The array of time for every CoolDownStage <br>
     *         0: WINDUP, 1: ACTIVE, 2: PASSIVE, 3: COOLDOWN
     */
    public float[] getTimeInSArray() {
        return timeInS;
    }

    /**
     * @throws IllegalArgumentException If the given index is not between or equal to 0 and 3
     *
     * @param index The index corresponding to the CoolDownStage
     * @return The time the coolDown lasts at the stage specified by index
     */
    public float getStageTimeInS(int index) {
        if (index < 0 || index > 3)
            throw new IllegalArgumentException("There are only four coolDownStages!");

        return timeInS[index];
    }

    /**
     * @param coolDownStage The CoolDownStage to get the time of
     * @return The time the coolDown lasts at that stage
     */
    public float getStageTimeInS(@NotNull Enums.CoolDownStage coolDownStage) {
        return timeInS[Maps.coolDownStageToIndexMap.get(coolDownStage)];
    }

    /**
     * @return The time the currently active coolDownStage takes
     */
    public float getStageTimeInS() {
        updateCoolDownStage();
        return timeInS[Maps.coolDownStageToIndexMap.get(coolDownStage)];
    }

    /**
     * @return The coolDownStage this is in
     */
    public @NotNull Enums.CoolDownStage getCoolDownStage() {
        updateCoolDownStage();
        return coolDownStage;
    }

    /**
     * @return The spellType this is coolDowning
     */
    public @NotNull String getSpellType() {
        return spellType;
    }

    /**
     * Compares the Objects for their data WITHOUT updating the coolDownStage
     *
     * @return If they share the same data
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CoolDownEntry) obj;
        return Objects.equals(this.spellType, that.spellType) &&
                Objects.equals(this.startDate, that.startDate) &&
                Arrays.equals(this.timeInS, that.timeInS) &&
                Objects.equals(this.coolDownStage, that.coolDownStage);
    }

    /**
     * Updates the coolDownStage, then generates the hashCode
     *
     * @return The Objects hashCode
     */
    @Override
    public int hashCode() {
        updateCoolDownStage();
        return Objects.hash(spellType, startDate, Arrays.hashCode(timeInS), coolDownStage);
    }

    /**
     * Updates the coolDownStage, then generates the String representation
     *
     * @return The Objects String representation
     */
    @Override
    public String toString() {
        updateCoolDownStage();
        return "CoolDownEntry[" +
                "spellType=" + spellType + ", " +
                "startDate=" + startDate + ", " +
                "timeInS=" + Arrays.toString(timeInS) + ", " +
                "coolDownStage=" + coolDownStage + ']';
    }
}
