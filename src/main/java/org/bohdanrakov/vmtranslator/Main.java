package org.bohdanrakov.vmtranslator;

import org.bohdanrakov.vmtranslator.commands.CommandType;

import java.util.List;

import static org.bohdanrakov.vmtranslator.commands.CommandType.POP;
import static org.bohdanrakov.vmtranslator.commands.CommandType.PUSH;

public class Main {

    private static final String ASSEMBLY_EXTENSION = ".asm";

    public static void main(String[] args) {

        String vmFileName = args[0];

        List<String> lines = FileUtil.readFileLines(vmFileName);
        Parser parser = new Parser(lines);
        String newAssemblyFileName = FileUtil.changeExtensionInFileName(vmFileName, ASSEMBLY_EXTENSION);
        CodeWriter codeWriter = new CodeWriter(newAssemblyFileName);

        List<String> asmCommands = translateVmToAsm(parser, codeWriter);

        FileUtil.writeLinesToNewFile(asmCommands, newAssemblyFileName);
    }

    private static List<String> translateVmToAsm(Parser parser, CodeWriter codeWriter) {
        while (parser.hasMoreCommands()) {
            parser.advance();
            CommandType currentCommandType = parser.commandType();
            if (currentCommandType.equals(PUSH) || currentCommandType.equals(POP)) {
                codeWriter.writePushPop(currentCommandType, parser.arg1(), parser.arg2());
            } else {
                codeWriter.writeArithmetic(parser.getCurrentCommand());
            }
        }
        return codeWriter.getResult();
    }
}
