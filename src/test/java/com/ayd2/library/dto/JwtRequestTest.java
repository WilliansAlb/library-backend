package com.ayd2.library.dto;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtRequestTest {
    @Test
    public void testRecordConstructorAndGetters() {
        // Create a record instance using the constructor
        JwtRequest jwtRequest = new JwtRequest("testToken");

        // Test the getters
        assertEquals("testToken", jwtRequest.token());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Create two record instances with the same values
        JwtRequest jwtRequest1 = new JwtRequest("testToken");
        JwtRequest jwtRequest2 = new JwtRequest("testToken");

        // Test equals() method
        assertEquals(jwtRequest1, jwtRequest2);

        // Test hashCode() method
        assertEquals(jwtRequest1.hashCode(), jwtRequest2.hashCode());
    }

    @Test
    public void testToString() {
        JwtRequest jwtRequest = new JwtRequest("testToken");

        // Test toString() method
        assertEquals("JwtRequest[token=testToken]", jwtRequest.toString());
    }
}
