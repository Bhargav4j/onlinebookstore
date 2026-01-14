package com.bittercode.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testSetAndGetEmailId() {
        String email = "test@example.com";
        user.setEmailId(email);
        assertEquals(email, user.getEmailId());
    }

    @Test
    void testSetAndGetPassword() {
        String password = "securePassword123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @Test
    void testSetAndGetFirstName() {
        String firstName = "John";
        user.setFirstName(firstName);
        assertEquals(firstName, user.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        String lastName = "Doe";
        user.setLastName(lastName);
        assertEquals(lastName, user.getLastName());
    }

    @Test
    void testSetAndGetPhone() {
        Long phone = 1234567890L;
        user.setPhone(phone);
        assertEquals(phone, user.getPhone());
    }

    @Test
    void testSetAndGetAddress() {
        String address = "123 Main St, City, Country";
        user.setAddress(address);
        assertEquals(address, user.getAddress());
    }

    @Test
    void testSetAndGetRoles() {
        List<UserRole> roles = Arrays.asList(UserRole.CUSTOMER, UserRole.SELLER);
        user.setRoles(roles);
        assertEquals(roles, user.getRoles());
        assertEquals(2, user.getRoles().size());
    }

    @Test
    void testSetAndGetRolesSingleRole() {
        List<UserRole> roles = Arrays.asList(UserRole.CUSTOMER);
        user.setRoles(roles);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(UserRole.CUSTOMER));
    }

    @Test
    void testUserIsSerializable() {
        assertTrue(user instanceof java.io.Serializable);
    }

    @Test
    void testDefaultValues() {
        User newUser = new User();
        assertNull(newUser.getEmailId());
        assertNull(newUser.getPassword());
        assertNull(newUser.getFirstName());
        assertNull(newUser.getLastName());
        assertNull(newUser.getPhone());
        assertNull(newUser.getAddress());
        assertNull(newUser.getRoles());
    }

    @Test
    void testSetEmailIdNull() {
        user.setEmailId(null);
        assertNull(user.getEmailId());
    }

    @Test
    void testSetPasswordNull() {
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    void testSetFirstNameNull() {
        user.setFirstName(null);
        assertNull(user.getFirstName());
    }

    @Test
    void testSetLastNameNull() {
        user.setLastName(null);
        assertNull(user.getLastName());
    }

    @Test
    void testSetPhoneNull() {
        user.setPhone(null);
        assertNull(user.getPhone());
    }

    @Test
    void testSetAddressNull() {
        user.setAddress(null);
        assertNull(user.getAddress());
    }

    @Test
    void testSetRolesNull() {
        user.setRoles(null);
        assertNull(user.getRoles());
    }

    @Test
    void testSetEmptyEmail() {
        user.setEmailId("");
        assertEquals("", user.getEmailId());
    }

    @Test
    void testSetEmptyPassword() {
        user.setPassword("");
        assertEquals("", user.getPassword());
    }

    @Test
    void testSetEmptyFirstName() {
        user.setFirstName("");
        assertEquals("", user.getFirstName());
    }

    @Test
    void testSetEmptyLastName() {
        user.setLastName("");
        assertEquals("", user.getLastName());
    }

    @Test
    void testSetEmptyAddress() {
        user.setAddress("");
        assertEquals("", user.getAddress());
    }

    @Test
    void testSetPhoneZero() {
        user.setPhone(0L);
        assertEquals(0L, user.getPhone());
    }

    @Test
    void testSetPhoneNegative() {
        user.setPhone(-1L);
        assertEquals(-1L, user.getPhone());
    }

    @Test
    void testSetLongEmail() {
        String longEmail = "verylongemailaddress@verylongdomainname.com";
        user.setEmailId(longEmail);
        assertEquals(longEmail, user.getEmailId());
    }

    @Test
    void testSetMultipleRoles() {
        List<UserRole> roles = Arrays.asList(UserRole.CUSTOMER, UserRole.SELLER);
        user.setRoles(roles);
        assertTrue(user.getRoles().contains(UserRole.CUSTOMER));
        assertTrue(user.getRoles().contains(UserRole.SELLER));
    }

    @Test
    void testSetEmptyRolesList() {
        List<UserRole> emptyRoles = Arrays.asList();
        user.setRoles(emptyRoles);
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void testMultipleSettersChaining() {
        user.setEmailId("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhone(1234567890L);
        user.setAddress("123 Main St");
        user.setPassword("password123");
        user.setRoles(Arrays.asList(UserRole.CUSTOMER));

        assertEquals("test@example.com", user.getEmailId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(1234567890L, user.getPhone());
        assertEquals("123 Main St", user.getAddress());
        assertEquals("password123", user.getPassword());
        assertEquals(1, user.getRoles().size());
    }
}
