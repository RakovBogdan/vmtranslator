package org.bohdanrakov.vmtranslator.commands;

public enum CommandType {

    ARITHMETIC, PUSH, POP, LABEL, GOTO, IF, FUNCTION, RETURN, CALL;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
