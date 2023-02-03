package me.chriss99.spellbend.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class Diagnostics {
    private ArrayList<String> potentialPaths = null;

    private LinkedList<ParsingLog> methodParsingLog = null;

    public Diagnostics() {}

    public void setPotentialPaths(ArrayList<String> potentialPaths) {
        this.potentialPaths = potentialPaths;
    }

    public void setMethodParsingLog(LinkedList<ParsingLog> methodParsingLog) {
        this.methodParsingLog = methodParsingLog;
    }

    public ArrayList<String> getPotentialPaths() {
        return potentialPaths;
    }

    public LinkedList<ParsingLog> getMethodParsingLog() {
        return methodParsingLog;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Diagnostics) obj;
        return Objects.equals(this.potentialPaths, that.potentialPaths) &&
                Objects.equals(this.methodParsingLog, that.methodParsingLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(potentialPaths, methodParsingLog);
    }

    @Override
    public String toString() {
        return "Diagnostics[" +
                "matchingPaths=" + potentialPaths + ", " +
                "methodParsingLog=" + methodParsingLog + ']';
    }

}
