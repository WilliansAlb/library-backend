package com.ayd2.library.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LibraryConstantTest {

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
}
