package org.bohdanrakov.vmtranslator;

import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.nio.file.Path;
import java.util.List;

import static org.bohdanrakov.vmtranslator.commands.CommandType.*;

public class Main {

    public static void main(String[] args) {
        CodeWriter codeWriter = new CodeWriter();
        codeWriter.writeInit();

        String resourceToParse = args[0];
        List<Path> filePaths = FileUtil.parseResourceToFileNames(resourceToParse);

        for (Path vmFilePath: filePaths) {
            List<String> lines = FileUtil.readFileLines(vmFilePath);
            Parser parser = new Parser(lines);
            codeWriter.setFileName(FileUtil.getFileNameWithoutExtension(vmFilePath.getFileName().toString()));
            translateVmToAsm(parser, codeWriter);
        }
        FileUtil.writeLinesToNewFile(codeWriter.getResult(), resourceToParse + ".asm");
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
            } else if (currentCommandType.equals(CALL)) {
                codeWriter.writeCall(parser.arg1(), parser.arg2());
            } else {
                codeWriter.writeArithmetic(parser.getCurrentCommand());
            }
        }
    }
}
