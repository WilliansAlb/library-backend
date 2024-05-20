package com.ayd2.library.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LibraryConstant {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private LibraryConstant(){

    }

    public static String usingCharacterToUpperCaseMethod(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return Arrays.stream(input.split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
