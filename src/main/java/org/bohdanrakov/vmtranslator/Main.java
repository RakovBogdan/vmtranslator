package org.bohdanrakov.vmtranslator;

import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.util.List;

import static org.bohdanrakov.vmtranslator.commands.CommandType.*;

public class Main {

    private static final String ASSEMBLY_EXTENSION = ".asm";

    public static void main(String[] args) {

        String vmFileName = args[0];

        List<String> lines = FileUtil.readFileLines(vmFileName);
        Parser parser = new Parser(lines);
        String newAssemblyFileName = FileUtil.changeExtensionInFileName(vmFileName, ASSEMBLY_EXTENSION);
        CodeWriter codeWriter = new CodeWriter(newAssemblyFileName);
        translateVmToAsm(parser, codeWriter);
    }

    private static void translateVmToAsm(Parser parser, CodeWriter codeWriter) {
        while (parser.hasMoreCommands()) {
            parser.advance();
            CommandType currentCommandType = parser.commandType();
            if (currentCommandType.equals(PUSH) || currentCommandType.equals(POP)) {
                codeWriter.writePushPop(currentCommandType, parser.arg1(), parser.arg2());
            } else if (currentCommandType.equals(LABEL)) {
                codeWriter.writeLabel(parser.arg1());
            } else if (currentCommandType.equals(GOTO)) {
                codeWriter.writeGoto(parser.arg1());
            } else if (currentCommandType.equals(IF)) {
                codeWriter.writeIf(parser.arg1());
            } else {
                codeWriter.writeArithmetic(parser.getCurrentCommand());
            }
        }
        codeWriter.write();
    }
}
