package me.chriss99.spellbend.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class Diagnostics {
    private ArrayList<String> possiblePaths = null;
    private LinkedList<ParsingLog> SubCommandParsingLog = null;

    public Diagnostics() {}

    public void setPossiblePaths(ArrayList<String> potentialPaths) {
        this.possiblePaths = potentialPaths;
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
