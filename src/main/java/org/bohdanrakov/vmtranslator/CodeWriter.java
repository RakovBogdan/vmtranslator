package org.bohdanrakov.vmtranslator;

import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.util.*;
import java.util.stream.Stream;

import static org.bohdanrakov.vmtranslator.commands.CommandType.PUSH;

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

        addCommandTemplateToMap("return", "return");
        addCommandTemplateToMap("call", "call");
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
    private int vmCommandJumpLabelIncrement = 0;
    private int functionReturnLabelIncrement = 0;
    private String currentFunctionName;
    private String fileName;

    public void writeInit() {
        result.add("@256");
        result.add("D=A");
        result.add("@SP");
        result.add("M=D");
        result.add("@Sys.init");
        result.add("0;JMP");
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void writeLabel(String label) {
        result.add("(" + currentFunctionName + "$" + label + ")");
    }

    public void writeGoto(String label) {
        result.add("@" + currentFunctionName + "$" + label);
        result.add("0;JMP");
    }

    public void writeIf(String label) {
        result.add("@SP");
        result.add("AM=M-1");
        result.add("D=M");
        result.add("@" + currentFunctionName + "$" + label);
        result.add("D;JNE");
    }

    public void writeFunction(String functionName, int variablesCount) {
        currentFunctionName = functionName;
        functionReturnLabelIncrement = 0;
        result.add("(" + functionName + ")");
        for (int i = 0; i < variablesCount; i++) {
            writePushPop(PUSH, "constant", 0);
        }
    }

    public void writeCall(String functionName, int argumentsCount) {
        List<String> asmInstructions = commandTemplates.get("call");
        String functionReturnLabel = currentFunctionName + "$ret" + functionReturnLabelIncrement;
        functionReturnLabelIncrement++;
        asmInstructions.set(0, "@" + functionReturnLabel);
        asmInstructions.set(37, "@" + String.valueOf(5 + argumentsCount));
        asmInstructions.set(45, "@" + functionName);
        asmInstructions.set(47, "(" + functionReturnLabel + ")");
        result.addAll(asmInstructions);
    }

    public void writeReturn() {
        List<String> asmInstructions = commandTemplates.get("return");
        result.addAll(asmInstructions);
    }


    public void writeArithmetic(String command) {
        List<String> asmInstructions = commandTemplates.get(command);
        if (command.equals("eq")) {
            asmInstructions.set(12, "@ISZERO" + String.valueOf(vmCommandJumpLabelIncrement));
            asmInstructions.set(15, "@SPPLUS" + String.valueOf(vmCommandJumpLabelIncrement));
            asmInstructions.set(17, "(ISZERO" + String.valueOf(vmCommandJumpLabelIncrement) + ")");
            asmInstructions.set(19, "(SPPLUS" + String.valueOf(vmCommandJumpLabelIncrement) + ")");
        } else if (command.equals("lt") || command.equals("gt")) {
            asmInstructions.set(12, "@LT" + String.valueOf(vmCommandJumpLabelIncrement));
            asmInstructions.set(15, "@SPPLUS" + String.valueOf(vmCommandJumpLabelIncrement));
            asmInstructions.set(17, "(LT" + String.valueOf(vmCommandJumpLabelIncrement) + ")");
            asmInstructions.set(19, "(SPPLUS" + String.valueOf(vmCommandJumpLabelIncrement) + ")");
        }

        result.addAll(asmInstructions);
        vmCommandJumpLabelIncrement++;
    }

    public void writePushPop(CommandType commandType, String memorySegment, int index) {
        String memorySegmentIndex = String.valueOf(index);
        List<String> asmInstructions = commandTemplates.get(commandType.toString() + UNDERSCORE + memorySegment);
        if (memorySegment.equals("constant")) {
            asmInstructions.set(0, "@" + memorySegmentIndex);
        } else if (memorySegment.equals("argument")) {
            asmInstructions.set(0, "@ARG");
            asmInstructions.set(2, "@" + index);
        } else if (memorySegment.equals("local")) {
            asmInstructions.set(0, "@LCL");
            asmInstructions.set(2, "@" + index);
        } else if (memorySegment.equals("this")) {
            asmInstructions.set(0, "@THIS");
            asmInstructions.set(2, "@" + index);
        } else if (memorySegment.equals("that")) {
            asmInstructions.set(0, "@THAT");
            asmInstructions.set(2, "@" + index);
        } else if (memorySegment.equals("temp")) {
            if (commandType.equals(PUSH)) {
                asmInstructions.set(0, "@" + String.valueOf(TEMP_SEGMENT_START_INDEX + index));
            } else if (commandType.equals(CommandType.POP)) {
                asmInstructions.set(3, "@" + String.valueOf(TEMP_SEGMENT_START_INDEX + index));
            }
        }
        else if (memorySegment.equals("pointer")) {
            if (commandType.equals(PUSH)) {
                if (index == 0) {
                    asmInstructions.set(0, "@" + "THIS");
                } else {
                    asmInstructions.set(0, "@" + "THAT");
                }
            }
            else if (commandType.equals(CommandType.POP)) {
                if (index == 0) {
                    asmInstructions.set(3, "@" + "THIS");
                } else {
                    asmInstructions.set(3, "@" + "THAT");
                }
            }
        }
        else if (memorySegment.equals("static")) {
            if (commandType.equals(PUSH)) {
                asmInstructions.set(0, "@" + fileName + "." + index);
            }
            else if (commandType.equals(CommandType.POP)) {
                asmInstructions.set(3, "@" + fileName + "." + index);
            }
        }

        result.addAll(asmInstructions);
        vmCommandJumpLabelIncrement++;
    }

    public List<String> getResult() {
        return result;
    }
}
