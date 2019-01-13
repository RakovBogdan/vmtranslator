package org.bohdanrakov.vmtranslator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.bohdanrakov.vmtranslator.commands.CommandType.PUSH;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CodeWriterTest {

    private CodeWriter testInstance;

    @Test
    public void testArithmetic() {
        List<String> expectedAdd = new ArrayList<>();
        expectedAdd.add("@SP");
        expectedAdd.add("M=M-1");
        expectedAdd.add("A=M");
        expectedAdd.add("D=M");
        expectedAdd.add("@R13");
        expectedAdd.add("M=D");
        expectedAdd.add("@SP");
        expectedAdd.add("M=M-1");
        expectedAdd.add("A=M");
        expectedAdd.add("D=M");
        expectedAdd.add("@R13");
        expectedAdd.add("D=D+M");
        expectedAdd.add("@SP");
        expectedAdd.add("A=M");
        expectedAdd.add("M=D");
        expectedAdd.add("@SP");
        expectedAdd.add("M=M+1");

        testInstance = new CodeWriter();
        testInstance.writeArithmetic("add");

        assertEquals(expectedAdd, testInstance.getResult());

    }

    @Test
    public void testPushConstant() {
        List<String> expectedAdd = new ArrayList<>();
        expectedAdd.add("@1");
        expectedAdd.add("D=A");
        expectedAdd.add("@SP");
        expectedAdd.add("A=M");
        expectedAdd.add("M=D");
        expectedAdd.add("@SP");
        expectedAdd.add("M=M+1");

        testInstance = new CodeWriter();
        testInstance.writePushPop(PUSH, "constant", 1);

        assertEquals(expectedAdd, testInstance.getResult());
    }
}