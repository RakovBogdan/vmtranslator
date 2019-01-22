package org.bohdanrakov.vmtranslator.commands;

public enum  MemorySegment {
    LOCAL, ARGUMENT, THIS, THAT, CONSTANT, STATIC, TEMP, POINTER;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
