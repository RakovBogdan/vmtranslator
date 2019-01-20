package org.bohdanrakov.vmtranslator;

import org.apache.commons.lang3.StringUtils;
import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bohdanrakov.vmtranslator.commands.CommandType.*;

public class Parser {

    private static final List<String> arithmeticCommandsNames = Stream.of("add",
            "and", "eq", "gt", "lt", "neg", "not", "or", "sub").collect(Collectors.toList());
    private static final String WHITESPACE = " ";
    private static final int COMMAND_TYPE_INDEX = 0;
    private List<String> commands;
    private String currentCommand;
    private int currentCommandIndex = 0;

    public Parser(List<String> lines) {
        List<String> commands = lines.stream().map(line -> {
            line = removeComments(line);
            return line;
        }).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        this.commands = commands;
    }

    private String removeComments(String line) {
        int commentPosition = line.indexOf("//");
        if (commentPosition != -1) {
            line = line.substring(0, commentPosition);
        }

        return line;
    }

    public boolean hasMoreCommands() {
        return currentCommandIndex < commands.size();
    }

    public void advance() {
        currentCommand = commands.get(currentCommandIndex);
        currentCommandIndex++;
    }

    public CommandType commandType() {
        String commandType = currentCommand.split(WHITESPACE)[COMMAND_TYPE_INDEX];
        if (arithmeticCommandsNames.contains(commandType)) {
            return CommandType.ARITHMETIC;
        }
        return CommandType.valueOf(commandType.toUpperCase());
    }

    public String arg1() {
        String[] commandSplitted = currentCommand.split(WHITESPACE);
        if (commandSplitted.length == 1) {
            return commandSplitted[0];
        }
        return commandSplitted[1];
    }

    public int arg2() {
        return Integer.valueOf(currentCommand.split(WHITESPACE)[2]);
    }

    public String getCurrentCommand() {
        return currentCommand;
    }
}
