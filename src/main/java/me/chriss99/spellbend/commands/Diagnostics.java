package me.chriss99.spellbend.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class Diagnostics {
    private ArrayList<Method> matchingPaths = null;

    private LinkedList<ParsingLog> methodParsingLog = null;

    public Diagnostics() {
    }

    public void setMatchingPaths(ArrayList<Method> matchingPaths) {
        this.matchingPaths = matchingPaths;
    }

    public void setMethodParsingLog(LinkedList<ParsingLog> methodParsingLog) {
        this.methodParsingLog = methodParsingLog;
    }

    public ArrayList<Method> getMatchingPaths() {
        return matchingPaths;
    }

    public LinkedList<ParsingLog> getMethodParsingLog() {
        return methodParsingLog;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Diagnostics) obj;
        return Objects.equals(this.matchingPaths, that.matchingPaths) &&
                Objects.equals(this.methodParsingLog, that.methodParsingLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchingPaths, methodParsingLog);
    }

    @Override
    public String toString() {
        return "Diagnostics[" +
                "matchingPaths=" + matchingPaths + ", " +
                "methodParsingLog=" + methodParsingLog + ']';
    }

}
