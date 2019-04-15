package org.bohdanrakov.vmtranslator;

import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.bohdanrakov.vmtranslator.commands.CommandType.*;

public class Main {

    private static final String ASSEMBLY_EXTENSION = ".asm";

    public static void main(String[] args) {
        CodeWriter codeWriter = new CodeWriter();
        codeWriter.writeInit();

        String resourceToParse = args[0];
        List<String> files = FileUtil.parseResourceToFiles(resourceToParse);

        for (String vmFileName: files) {
            List<String> lines = FileUtil.readFileLines(vmFileName);
            Parser parser = new Parser(lines);
            codeWriter.setFileName(vmFileName);
            translateVmToAsm(parser, codeWriter);
        }
        FileUtil.writeLinesToNewFile(codeWriter.getResult(), resourceToParse + ".vm");
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
            } else if (currentCommandType.equals(FUNCTION)) {
                codeWriter.writeFunction(parser.arg1(), parser.arg2());
            } else if (currentCommandType.equals(RETURN)) {
                codeWriter.writeReturn();
            } else {
                codeWriter.writeArithmetic(parser.getCurrentCommand());
            }
        }
    }
}
