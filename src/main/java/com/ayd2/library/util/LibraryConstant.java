package com.ayd2.library.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LibraryConstant {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final double PAYMENT_NORMAL = 5;
    public static final double PAYMENT_LATE = 15;
    public static final double PAYMENT_SANCTION = 150;

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

    public static boolean isNumber(String strNum) {
        if (strNum == null || strNum.isEmpty()) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
