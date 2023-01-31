package me.chriss99.spellbend.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class ReflectiveCommandBase extends BukkitCommand implements CommandExecutor {
    private final HashMap<String[], ArrayList<Method>> pathToMethodsMap = new HashMap<>();
    private int longestPath = 0;

    public ReflectiveCommandBase(@NotNull String commandName, @NotNull String description, @NotNull List<String> aliases) {
        super(commandName, description,
                "If this shows up, someone has not updated the usageMessage in ReflectiveCommandBase yet. Please notify a developer immediately!", aliases);

        for (Method method : getClass().getDeclaredMethods())
            if (method.isAnnotationPresent(ReflectCommand.class)) {
                String[] path = method.getAnnotation(ReflectCommand.class).path();
                if (longestPath < path.length)
                    longestPath = path.length;

                ArrayList<Method> methods = pathToMethodsMap.get(path);

                if (methods == null) {
                    pathToMethodsMap.put(path, new ArrayList<>(List.of(method)));
                    continue;
                }

                methods.add(method);
            }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] arguments) {
        return false;
    }

    private @NotNull ArrayList<Method> getFittingMethods(final @NotNull String[] arguments) {
        return new ArrayList<>(methodsMatchingArgumentCount(getPathMatchingMethods(arguments), arguments.length));
    }

    private @NotNull LinkedList<Method> getPathMatchingMethods(final @NotNull String[] arguments) {
        LinkedList<Method> methods = new LinkedList<>();
        String[] path;

        for (int i = 0; i < arguments.length && i < longestPath; i++) {
            path = new String[i+1];
            System.arraycopy(arguments, 0, path, 0, path.length);

            methods.addAll(pathToMethodsMap.get(path));
        }

        return methods;
    }

    private @NotNull LinkedList<Method> methodsMatchingArgumentCount(final @NotNull LinkedList<Method> methods, final int argumentCount) {
        methods.removeIf(method -> method.getParameterCount() != argumentCount - method.getAnnotation(ReflectCommand.class).path().length);
        return methods;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments) {
        return execute(sender, alias, arguments);
    }
}
