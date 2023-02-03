package me.chriss99.spellbend.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class PreParsingMethod {
    private final Method method;
    private final Class<?>[] parsingParameterTypes;
    private final Class<?> senderParameterType;

    public PreParsingMethod(@NotNull Method method) {
        this.method = method;
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            parsingParameterTypes = new Class[0];
            senderParameterType = null;
            return;
        }

        if ((parameters[0].getType().equals(CommandSender.class) || parameters[0].getType().equals(Player.class)) && parameters[0].getName().equals("commandSender")) {
            if (parameters.length == 1)
                parsingParameterTypes = new Class[0];
            else parsingParameterTypes = Arrays.copyOfRange(method.getParameterTypes(), 1, parameters.length);
            senderParameterType = parameters[0].getType();
            return;
        }

        parsingParameterTypes = method.getParameterTypes();
        senderParameterType = null;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParsingParameterTypes() {
        return parsingParameterTypes;
    }

    public Class<?> getSenderParameterType() {
        return senderParameterType;
    }
}
