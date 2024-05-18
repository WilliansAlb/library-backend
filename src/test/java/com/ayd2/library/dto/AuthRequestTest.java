package com.ayd2.library.dto;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class AuthRequestTest {
    @Test
    public void testRecordConstructorAndGetters() {
        // Create a record instance using the constructor
        AuthRequest authReqDto = new AuthRequest("testUser", "testPassword");

        // Test the getters
        assertEquals("testUser", authReqDto.username());
        assertEquals("testPassword", authReqDto.password());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Create two record instances with the same values
        AuthRequest authReqDto1 = new AuthRequest("testUser", "testPassword");
        AuthRequest authReqDto2 = new AuthRequest("testUser", "testPassword");

        // Test equals() method
        assertEquals(authReqDto1, authReqDto2);

        // Test hashCode() method
        assertEquals(authReqDto1.hashCode(), authReqDto2.hashCode());
    }

    @Test
    public void testToString() {
        AuthRequest authReqDto = new AuthRequest("testUser", "testPassword");

        // Test toString() method
        assertEquals("AuthRequest[username=testUser, password=testPassword]", authReqDto.toString());
    }
}
