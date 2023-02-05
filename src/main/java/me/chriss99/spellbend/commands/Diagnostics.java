package me.chriss99.spellbend.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class Diagnostics {
    private ArrayList<String> possiblePaths = null;
    private LinkedList<ParsingLog> methodParsingLog = null;

    public Diagnostics() {}

    public void setPossiblePaths(ArrayList<String> potentialPaths) {
        this.possiblePaths = potentialPaths;
    }

    public void setMethodParsingLog(LinkedList<ParsingLog> methodParsingLog) {
        this.methodParsingLog = methodParsingLog;
    }

    public ArrayList<String> getPossiblePaths() {
        return possiblePaths;
    }

    public LinkedList<ParsingLog> getMethodParsingLog() {
        return methodParsingLog;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Diagnostics) obj;
        return Objects.equals(this.possiblePaths, that.possiblePaths) &&
                Objects.equals(this.methodParsingLog, that.methodParsingLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possiblePaths, methodParsingLog);
    }

    @Override
    public String toString() {
        return "Diagnostics[" +
                "possiblePaths=" + possiblePaths + ", " +
                "methodParsingLog=" + methodParsingLog + ']';
    }

}
