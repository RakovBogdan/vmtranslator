package org.bohdanrakov.vmtranslator;

import org.apache.commons.io.IOUtils;
import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CodeWriter {

    private static final String TXT_EXTENSION = ".txt";
    private static final String UTF_8 = "UTF-8";
    private static final String DIRECTORY_SEPARATOR = "/";
    private static Map<String, List<String>> arithmeticCommands;
    private static Map<String, List<String>> memorySegmentCommands;

    private static final String ADD = "add";
    private static final String SUB = "sub";
    private static final String EQ = "eq";
    private static final String LT = "lt";
    private static final String GT = "gt";
    private static final String AND = "and";
    private static final String OR = "or";
    private static final String NEG = "neg";
    private static final String NOT = "not";
    private static final String CONSTANT = "constant";

    static {
        arithmeticCommands = new HashMap<>();
        memorySegmentCommands = new HashMap<>();
        Stream.of(ADD, SUB, EQ, LT, GT, AND, OR, NEG, NOT).forEach(CodeWriter::addArithmeticCommandToMap);
        Stream.of(CONSTANT).forEach(command -> addMemorySegmentCommand(CommandType.PUSH, command));
    }

    private static void addArithmeticCommandToMap(String commandKey) {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        try {
            List<String> asmInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream(commandKey + TXT_EXTENSION), UTF_8);
            arithmeticCommands.put(commandKey, asmInstructions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addMemorySegmentCommand(CommandType commandType, String command) {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        try {
            List<String> asmInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream(commandType.toString().toLowerCase() + DIRECTORY_SEPARATOR
                            + command + TXT_EXTENSION), UTF_8);
            memorySegmentCommands.put(command, asmInstructions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> result = new ArrayList<>();

    public void writeArithmetic(String command) {
        List<String> asmInstructions = arithmeticCommands.get(command);
        result.addAll(asmInstructions);
    }

    public void writePushPop(CommandType commandType, String memorySegment, int index) {
        String memorySegmentIndex = String.valueOf(index);
        List<String> asmInstructions = memorySegmentCommands.get(memorySegment);
        if (memorySegment.equals(CONSTANT)) {
            asmInstructions.set(0, "@" + memorySegmentIndex);
        }

        result.addAll(asmInstructions);
    }

    public List<String> getResult() {
        return result;
    }
}
