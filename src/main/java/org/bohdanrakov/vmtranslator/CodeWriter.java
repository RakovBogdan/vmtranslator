package org.bohdanrakov.vmtranslator;

import org.apache.commons.io.IOUtils;
import org.bohdanrakov.vmtranslator.commands.ArithmeticCommand;
import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.bohdanrakov.vmtranslator.commands.ArithmeticCommand.*;
import static org.bohdanrakov.vmtranslator.commands.CommandType.*;
import static org.bohdanrakov.vmtranslator.commands.MemorySegment.*;

public class CodeWriter {

    private static final String TXT_EXTENSION = ".txt";
    private static final String UTF_8 = "UTF-8";
    private static final String UNDERSCORE = "_";
    private static Map<String, List<String>> commands;

    static {
        commands = new HashMap<>();
        Arrays.stream(ArithmeticCommand.values()).forEach(
                arithmeticCommand -> addCommandToMap(arithmeticCommand.toString()));
        Stream.of(PUSH.toString() + UNDERSCORE + CONSTANT.toString()).forEach(CodeWriter::addCommandToMap);
    }

    private List<String> result = new ArrayList<>();
    private int currentStackCommandIndex = 0;

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

    public void writeArithmetic(String command) {
        List<String> asmInstructions = commands.get(command);
        if (command.equals(EQ.toString())) {
            asmInstructions.set(12, "@ISZERO" + String.valueOf(currentStackCommandIndex));
            asmInstructions.set(15, "@SPPLUS" + String.valueOf(currentStackCommandIndex));
            asmInstructions.set(17, "(ISZERO" + String.valueOf(currentStackCommandIndex) + ")");
            asmInstructions.set(19, "(SPPLUS" + String.valueOf(currentStackCommandIndex) + ")");
        }
        if (command.equals(LT.toString()) | command.equals(GT.toString())) {
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
        if (memorySegment.equals(CONSTANT.toString())) {
            asmInstructions.set(0, "@" + memorySegmentIndex);
        }

        result.addAll(asmInstructions);
        currentStackCommandIndex++;
    }

    public List<String> getResult() {
        return result;
    }
}
