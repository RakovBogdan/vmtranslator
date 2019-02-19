package org.bohdanrakov.vmtranslator;

import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CodeWriter {

    private static final String UNDERSCORE = "_";
    private static final int TEMP_SEGMENT_START_INDEX = 5;
    private static Map<String, List<String>> commandTemplates;

    static {
        commandTemplates = new HashMap<>();
        Stream.of("add", "sub", "eq", "lt", "gt", "and", "or", "neg", "not",
                "push_temp", "pop_temp", "push_static", "pop_static", "push_pointer", "pop_pointer", "push_constant")
                .forEach(CodeWriter::addCommandTemplateToMap);

        Stream.of("local", "argument", "this", "that").forEach(memorySegment -> {
            addCommandTemplateToMap("push_segment", "push_" + memorySegment);
            addCommandTemplateToMap("pop_segment", "pop_" + memorySegment);
        });
    }

    private static void addCommandTemplateToMap(String templateName, String templateKey) {
        List<String> instructionsFromTemplate = FileUtil.getLinesFromResource(templateName);
        commandTemplates.put(templateKey, instructionsFromTemplate);
    }

    private static void addCommandTemplateToMap(String templateName) {
        List<String> instructionsFromTemplate = FileUtil.getLinesFromResource(templateName);
        commandTemplates.put(templateName, instructionsFromTemplate);
    }

    private List<String> result = new ArrayList<>();
    private int currentStackCommandIndex = 0;
    private String fileName;
    private String fileNameWithoutExtension;

    public CodeWriter(String filename) {
        this.fileName = filename;
        this.fileNameWithoutExtension = FileUtil.getFileNameWithoutExtension(fileName);
    }

    public void writeArithmetic(String command) {
        List<String> asmInstructions = commandTemplates.get(command);
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
        List<String> asmInstructions = commandTemplates.get(commandType.toString() + UNDERSCORE + memorySegment);
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
                asmInstructions.set(0, "@" + fileNameWithoutExtension + "." + index);
            }
            if (commandType.equals(CommandType.POP)) {
                asmInstructions.set(3, "@" + fileNameWithoutExtension + "." + index);
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
