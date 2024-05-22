package com.ayd2.library.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryConstantTest {
    public static final String NUMBER_NULL = null;
    public static final String NUMBER_CORRECT = "32";
    public static final String NUMBER_INCORRECT = "32a";

    @Test
    void testCapitalizationCorrect() {
        String input = "hello world";
        assertEquals("Hello World", LibraryConstant.usingCharacterToUpperCaseMethod(input));
    }

    @Test
    void testCapitalizationNotGivenString() {
        assertNull(LibraryConstant.usingCharacterToUpperCaseMethod(null));
    }

    @Test
    void testCapitalizationGivenEmptyString() {
        assertNull(LibraryConstant.usingCharacterToUpperCaseMethod(""));
    }

    @Test
    void testIsNumberSuccessfully(){
        assertTrue(LibraryConstant.isNumber(NUMBER_CORRECT));
    }

    @Test
    void testIsNotNumberSuccessfully(){
        assertFalse(LibraryConstant.isNumber(NUMBER_INCORRECT));
    }

    @Test
    void testIsNumberSendNull(){
        assertFalse(LibraryConstant.isNumber(null));
    }

    @Test
    void testIsNumberSendEmpty(){
        assertFalse(LibraryConstant.isNumber(""));
    }
}
