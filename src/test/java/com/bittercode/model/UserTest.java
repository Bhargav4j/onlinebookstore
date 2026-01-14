package com.bittercode.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void testGetEmailId() {
        user.setEmailId("test@example.com");
        assertEquals("test@example.com", user.getEmailId());
    }

    @Test
    public void testSetEmailId() {
        user.setEmailId("user@test.com");
        assertEquals("user@test.com", user.getEmailId());
    }

    @Test
    public void testGetPassword() {
        user.setPassword("password123");
        assertEquals("password123", user.getPassword());
    }

    @Test
    public void testSetPassword() {
        user.setPassword("newpass");
        assertEquals("newpass", user.getPassword());
    }

    @Test
    public void testGetFirstName() {
        user.setFirstName("John");
        assertEquals("John", user.getFirstName());
    }

    @Test
    public void testSetFirstName() {
        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName());
    }

    @Test
    public void testGetLastName() {
        user.setLastName("Doe");
        assertEquals("Doe", user.getLastName());
    }

    @Test
    public void testSetLastName() {
        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());
    }

    @Test
    public void testGetPhone() {
        user.setPhone(1234567890L);
        assertEquals(1234567890L, user.getPhone());
    }

    @Test
    public void testSetPhone() {
        user.setPhone(9876543210L);
        assertEquals(9876543210L, user.getPhone());
    }

    @Test
    public void testGetAddress() {
        user.setAddress("123 Main St");
        assertEquals("123 Main St", user.getAddress());
    }

    @Test
    public void testSetAddress() {
        user.setAddress("456 Oak Ave");
        assertEquals("456 Oak Ave", user.getAddress());
    }

    @Test
    public void testGetRoles() {
        List<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.CUSTOMER);
        user.setRoles(roles);
        assertEquals(1, user.getRoles().size());
        assertEquals(UserRole.CUSTOMER, user.getRoles().get(0));
    }

    @Test
    public void testSetRoles() {
        List<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.SELLER);
        user.setRoles(roles);
        assertNotNull(user.getRoles());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testSetNullEmailId() {
        user.setEmailId(null);
        assertNull(user.getEmailId());
    }

    @Test
    public void testSetNullPassword() {
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    public void testSetNullPhone() {
        user.setPhone(null);
        assertNull(user.getPhone());
    }

    @Test
    public void testSetEmptyRoles() {
        user.setRoles(new ArrayList<>());
        assertNotNull(user.getRoles());
        assertEquals(0, user.getRoles().size());
    }
}
