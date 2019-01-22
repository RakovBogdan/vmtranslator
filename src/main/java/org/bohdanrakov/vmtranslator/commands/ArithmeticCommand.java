package org.bohdanrakov.vmtranslator.commands;

public enum ArithmeticCommand {
    ADD, SUB, EQ, LT, GT, AND, OR, NEG, NOT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
