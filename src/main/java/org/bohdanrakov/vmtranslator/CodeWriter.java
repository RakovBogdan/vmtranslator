package org.bohdanrakov.vmtranslator;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CodeWriter {

    private static final String TXT_EXTENSION = ".txt";
    private static final String UTF_8 = "UTF-8";
    private static Map<String, List<String>> commands;

    private static final String ADD = "add";
    private static final String SUB = "sub";
    private static final String EQ = "eq";
    private static final String LT = "lt";
    private static final String GT = "gt";
    private static final String AND = "and";
    private static final String OR = "or";
    private static final String NEG = "neg";
    private static final String NOT = "not";

    static {
        commands = new HashMap<>();
        Stream.of(ADD, SUB, EQ, LT, GT, AND, OR, NEG, NOT).forEach(CodeWriter::addCommandToMap);
    }

    private static void addCommandToMap(String commandKey) {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        try {
            List<String> asmInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream(commandKey + TXT_EXTENSION), UTF_8);
            commands.put(commandKey, asmInstructions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> result = new ArrayList<>();

    public void writeArithmetic(String command) {
        List<String> asmInstructions = commands.get(command);
        result.addAll(asmInstructions);
    }

    public void writePushPop(String memorySegment, int index) {

    }

    public List<String> getResult() {
        return result;
    }
}
