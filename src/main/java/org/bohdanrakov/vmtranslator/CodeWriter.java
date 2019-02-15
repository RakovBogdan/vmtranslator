package org.bohdanrakov.vmtranslator;

import org.apache.commons.io.IOUtils;
import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CodeWriter {

    private static final String TXT_EXTENSION = ".txt";
    private static final String UTF_8 = "UTF-8";
    private static final String UNDERSCORE = "_";
    private static final List<String> arithmeticCommandsNames = Stream.of(
            "add", "sub", "eq", "lt", "gt", "and", "or", "neg", "not").collect(Collectors.toList());
    private static final int TEMP_SEGMENT_START_INDEX = 5;
    private static Map<String, List<String>> commands;

    static {
        commands = new HashMap<>();
        arithmeticCommandsNames.forEach(CodeWriter::addArithmeticCommandToMap);
        addConstantCommand();
        Stream.of("local", "argument", "this", "that").forEach(CodeWriter::addMemorySegmentCommands);
        addTempCommand();
        addPointerCommand();
        addStaticCommand();
    }

    public CodeWriter(String filename) {
        this.fileName = filename;
    }

    private List<String> result = new ArrayList<>();
    private int currentStackCommandIndex = 0;
    private String fileName;

    private static void addArithmeticCommandToMap(String commandKey) {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        try {
            List<String> asmInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream(commandKey + TXT_EXTENSION), UTF_8);
            commands.put(commandKey, asmInstructions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addMemorySegmentCommands(String memorySegment) {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        List<String> asmPushInstructions;
        List<String> asmPopInstructions;
        try {
            asmPushInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream("push_argument" + TXT_EXTENSION), UTF_8);
            asmPopInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream("pop_argument" + TXT_EXTENSION), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        commands.put("push" + UNDERSCORE + memorySegment, asmPushInstructions);
        commands.put("pop" + UNDERSCORE + memorySegment, asmPopInstructions);
    }

    private static void addConstantCommand() {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        try {
            List<String> asmInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream("push_constant" + TXT_EXTENSION), UTF_8);
            commands.put("push_constant", asmInstructions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addTempCommand() {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        List<String> asmPushInstructions;
        List<String> asmPopInstructions;
        try {
            asmPushInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream("push_temp" + TXT_EXTENSION), UTF_8);
            asmPopInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream("pop_temp" + TXT_EXTENSION), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        commands.put("push_temp", asmPushInstructions);
        commands.put("pop_temp", asmPopInstructions);
    }

    private static void addPointerCommand() {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        List<String> asmPushInstructions;
        List<String> asmPopInstructions;
        try {
            asmPushInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream("push_pointer" + TXT_EXTENSION), UTF_8);
            asmPopInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream("pop_pointer" + TXT_EXTENSION), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        commands.put("push_pointer", asmPushInstructions);
        commands.put("pop_pointer", asmPopInstructions);
    }

    private static void addStaticCommand() {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        List<String> asmPushInstructions;
        List<String> asmPopInstructions;
        try {
            asmPushInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream("push_static" + TXT_EXTENSION), UTF_8);
            asmPopInstructions = IOUtils.readLines(
                    classLoader.getResourceAsStream("pop_static" + TXT_EXTENSION), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        commands.put("push_static", asmPushInstructions);
        commands.put("pop_static", asmPopInstructions);
    }

    public void writeArithmetic(String command) {
        List<String> asmInstructions = commands.get(command);
        if (command.equals("eq")) {
            asmInstructions.set(12, "@ISZERO" + String.valueOf(currentStackCommandIndex));
            asmInstructions.set(15, "@SPPLUS" + String.valueOf(currentStackCommandIndex));
            asmInstructions.set(17, "(ISZERO" + String.valueOf(currentStackCommandIndex) + ")");
            asmInstructions.set(19, "(SPPLUS" + String.valueOf(currentStackCommandIndex) + ")");
        }
        if (command.equals("lt") || command.equals("gt")) {
            asmInstructions.set(12, "@LT" + String.valueOf(currentStackCommandIndex));
            asmInstructions.set(15, "@SPPLUS" + String.valueOf(currentStackCommandIndex));
            asmInstructions.set(17, "(LT" + String.valueOf(currentStackCommandIndex) + ")");
            asmInstructions.set(19, "(SPPLUS" + String.valueOf(currentStackCommandIndex) + ")");
        }

        result.addAll(asmInstructions);
        currentStackCommandIndex++;
    }

    public void writePushPop(CommandType commandType, String memorySegment, int index) {
        String memorySegmentIndex = String.valueOf(index);
        List<String> asmInstructions = commands.get(commandType.toString() + UNDERSCORE + memorySegment);
        if (memorySegment.equals("constant")) {
            asmInstructions.set(0, "@" + memorySegmentIndex);
        }
        if (memorySegment.equals("argument")) {
            asmInstructions.set(0, "@ARG");
            asmInstructions.set(2, "@" + index);
        }
        if (memorySegment.equals("local")) {
            asmInstructions.set(0, "@LCL");
            asmInstructions.set(2, "@" + index);
        }
        if (memorySegment.equals("this")) {
            asmInstructions.set(0, "@THIS");
            asmInstructions.set(2, "@" + index);
        }
        if (memorySegment.equals("that")) {
            asmInstructions.set(0, "@THAT");
            asmInstructions.set(2, "@" + index);
        }
        if (memorySegment.equals("temp")) {
            if (commandType.equals(CommandType.PUSH)) {
                asmInstructions.set(0, "@" + String.valueOf(TEMP_SEGMENT_START_INDEX + index));
            }
            if (commandType.equals(CommandType.POP)) {
                asmInstructions.set(3, "@" + String.valueOf(TEMP_SEGMENT_START_INDEX + index));
            }
        }
        if (memorySegment.equals("pointer")) {
            if (commandType.equals(CommandType.PUSH)) {
                if (index == 0) {
                    asmInstructions.set(0, "@" + "THIS");
                } else {
                    asmInstructions.set(0, "@" + "THAT");
                }
            }
            if (commandType.equals(CommandType.POP)) {
                if (index == 0) {
                    asmInstructions.set(3, "@" + "THIS");
                } else {
                    asmInstructions.set(3, "@" + "THAT");
                }
            }
        }
        if (memorySegment.equals("static")) {
            if (commandType.equals(CommandType.PUSH)) {

            }
            if (commandType.equals(CommandType.POP)) {

            }
        }

        result.addAll(asmInstructions);
        currentStackCommandIndex++;
    }

    public void write() {
        FileUtil.writeLinesToNewFile(result, fileName);
    }

    public List<String> getResult() {
        return result;
    }
}
