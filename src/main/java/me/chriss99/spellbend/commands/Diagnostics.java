package me.chriss99.spellbend.commands;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class Diagnostics {
    private final ArrayList<String> possiblePaths;
    private LinkedList<ParsingLog> SubCommandParsingLog = null;

    /**
     * Constructs a Diagnostics object and automatically generates the possible paths
     *
     * @param arguments The arguments given to the command to diagnose
     * @param maxPathLength The maximal amount of path arguments possible in the command
     */
    public Diagnostics(final @NotNull String[] arguments, int maxPathLength) {
        possiblePaths = new ArrayList<>(maxPathLength);

        for (int i = 0; i < arguments.length && i < maxPathLength; i++) {
            String path = String.join(" ", Arrays.copyOfRange(arguments, 0, i + 1)).toUpperCase();
            possiblePaths.add(path);
        }
    }

    public void setSubCommandParsingLog(LinkedList<ParsingLog> subCommandParsingLog) {
        this.SubCommandParsingLog = subCommandParsingLog;
    }

    public ArrayList<String> getPossiblePaths() {
        return possiblePaths;
    }

    public LinkedList<ParsingLog> getSubCommandParsingLog() {
        return SubCommandParsingLog;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Diagnostics) obj;
        return Objects.equals(this.possiblePaths, that.possiblePaths) &&
                Objects.equals(this.SubCommandParsingLog, that.SubCommandParsingLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possiblePaths, SubCommandParsingLog);
    }

    @Override
    public String toString() {
        return "Diagnostics[" +
                "possiblePaths=" + possiblePaths + ", " +
                "methodParsingLog=" + SubCommandParsingLog + ']';
    }

}
