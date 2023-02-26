package me.chriss99.spellbend.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SubCommand {
    private final Method method;
    private final String[] cleanPath;
    private final String arguments;
    private final Class<?>[] parsingParameterTypes;
    private final Class<?> senderParameterType;

    public SubCommand(@NotNull Method method, @NotNull String commandName) {
        this.method = method;

        cleanPath = getCleanPathFromString(method.getAnnotation(ReflectCommand.class).path()).toArray(new String[0]);

        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            parsingParameterTypes = new Class[0];
            senderParameterType = null;
            arguments = generateMethodArguments(commandName);
            return;
        }

        if ((parameters[0].getType().equals(CommandSender.class) || parameters[0].getType().equals(Player.class)) && parameters[0].getName().equals("commandSender")) {
            if (parameters.length == 1)
                parsingParameterTypes = new Class[0];
            else parsingParameterTypes = Arrays.copyOfRange(method.getParameterTypes(), 1, parameters.length);
            senderParameterType = parameters[0].getType();
            arguments = generateMethodArguments(commandName);
            return;
        }

        parsingParameterTypes = method.getParameterTypes();
        senderParameterType = null;
        arguments = generateMethodArguments(commandName);
    }

    private @NotNull String generateMethodArguments(@NotNull String commandName) {
        StringBuilder stringBuilder = new StringBuilder().append(commandName).append(" ").append(String.join(" ", cleanPath)).append(" ");
        Parameter[] parameters = method.getParameters();
        for (int i = (senderParameterType == null) ? 0 : 1; i < parameters.length; i++)
            stringBuilder.append("<").append(parameters[i].getName()).append("> ");
        stringBuilder.replace(stringBuilder.length()-1, stringBuilder.length(), "");

        return stringBuilder.toString();
    }

    public static @NotNull LinkedList<String> getCleanPathFromString(@NotNull String path) {
        LinkedList<String> splitPath = new LinkedList<>(Arrays.asList(path.split(" ")));
        splitPath.removeAll(List.of(""));
        return splitPath;
    }

    public Method getMethod() {
        return method;
    }

    public String[] getCleanPath() {
        return cleanPath;
    }

    public String getPath() {
        return String.join(" ", cleanPath);
    }

    public int getPathLength() {
        return cleanPath.length;
    }

    public String getArguments() {
        return arguments;
    }

    public Class<?>[] getParsingParameterTypes() {
        return parsingParameterTypes;
    }

    public Class<?> getSenderParameterType() {
        return senderParameterType;
    }
}
