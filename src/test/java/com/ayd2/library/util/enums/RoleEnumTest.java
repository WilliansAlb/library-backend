package com.ayd2.library.util.enums;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RoleEnumTest {

    @Test
    public void testEnumValues() {
        // Ensure the enum constants are defined correctly
        assertEquals("0", RoleEnum.LIBRARIAN.roleId);
        assertEquals("1", RoleEnum.STUDENT.roleId);
    }

    @Test
    public void testEnumConstantNames() {
        // Ensure the enum constant names are correct
        assertNotNull(RoleEnum.valueOf("LIBRARIAN"));
        assertNotNull(RoleEnum.valueOf("STUDENT"));
    }

    @Test
    public void testRoleId() {
        // Ensure the roleId field is correct for each enum constant
        assertEquals("0", RoleEnum.LIBRARIAN.roleId);
        assertEquals("1", RoleEnum.STUDENT.roleId);
    }

    @Test
    public void testEnumValuesMethod() {
        // Ensure the values() method returns the correct enum constants
        RoleEnum[] values = RoleEnum.values();
        assertEquals(2, values.length);
        assertEquals(RoleEnum.LIBRARIAN, values[0]);
        assertEquals(RoleEnum.STUDENT, values[1]);
    }

    @Test
    public void testEnumToString() {
        // Ensure the toString() method returns the correct name
        assertEquals("LIBRARIAN", RoleEnum.LIBRARIAN.toString());
        assertEquals("STUDENT", RoleEnum.STUDENT.toString());
    }
}
