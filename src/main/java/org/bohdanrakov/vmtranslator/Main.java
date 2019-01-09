package org.bohdanrakov.vmtranslator;

import java.util.List;

public class Main {

    private static final String HACK_EXTENSION = ".hack";

    public static void main(String[] args) {
        //Assembler assembler = new Assembler();

        String assemblyFilename = args[0];

        List<String> lines = FileUtil.readFileLines(assemblyFilename);
        //List<String> assemblyResult = assembler.assemble(lines);

        String newHackFileName = FileUtil.changeExtensionInFileName(assemblyFilename, HACK_EXTENSION);
        //FileUtil.writeLinesToNewFile(assemblyResult, newHackFileName);
    }
}
