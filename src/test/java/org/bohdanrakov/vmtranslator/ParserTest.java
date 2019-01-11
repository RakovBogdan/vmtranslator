package org.bohdanrakov.vmtranslator;

import org.bohdanrakov.vmtranslator.commands.CommandType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.bohdanrakov.vmtranslator.commands.CommandType.PUSH;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ParserTest {

    private Parser testInstance;

    @Test
    public void testGetCommandType() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("push local 5");
        testInstance = new Parser(lines);
        testInstance.advance();
        CommandType actual = testInstance.commandType();
        assertEquals(PUSH, actual);
    }

    @Test
    public void testArgument1WithPush() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("push local 5");
        testInstance = new Parser(lines);
        testInstance.advance();
        String actual = testInstance.arg1();
        assertEquals("local", actual);
    }

    @Test
    public void testArgument1WithArithmetic() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("add");
        testInstance = new Parser(lines);
        testInstance.advance();
        String actual = testInstance.arg1();
        assertEquals("add", actual);
    }

    @Test
    public void testArgument2() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("push local 5");
        testInstance = new Parser(lines);
        testInstance.advance();
        int actual = testInstance.arg2();
        assertEquals(5, actual);
    }
}