package org.bohdanrakov.vmtranslator;

import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.util.List;

import static org.bohdanrakov.vmtranslator.commands.CommandType.*;

public class Parser {

    private List<String> commands;
    private String currentCommand;
    private int currentCommandIndex = 0;

    public Parser(List<String> commands) {
        this.commands = commands;
    }

    public boolean hasMoreCommands() {
        return currentCommandIndex < commands.size();
    }

    public void advance() {
        currentCommand = commands.get(currentCommandIndex);
        currentCommandIndex++;
    }

    public CommandType commandType() {
        return POP;
    }


}
