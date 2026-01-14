package com.bittercode.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserRoleTest {

    @Test
    public void testCustomerRole() {
        UserRole role = UserRole.CUSTOMER;
        assertNotNull(role);
        assertEquals("CUSTOMER", role.name());
    }

    @Test
    public void testSellerRole() {
        UserRole role = UserRole.SELLER;
        assertNotNull(role);
        assertEquals("SELLER", role.name());
    }

    @Test
    public void testValueOfCustomer() {
        UserRole role = UserRole.valueOf("CUSTOMER");
        assertEquals(UserRole.CUSTOMER, role);
    }

    @Test
    public void testValueOfSeller() {
        UserRole role = UserRole.valueOf("SELLER");
        assertEquals(UserRole.SELLER, role);
    }

    @Test
    public void testValues() {
        UserRole[] roles = UserRole.values();
        assertEquals(2, roles.length);
        assertEquals(UserRole.CUSTOMER, roles[0]);
        assertEquals(UserRole.SELLER, roles[1]);
    }

    @Test
    public void testToString() {
        assertEquals("CUSTOMER", UserRole.CUSTOMER.toString());
        assertEquals("SELLER", UserRole.SELLER.toString());
    }

    @Test
    public void testComparison() {
        assertNotEquals(UserRole.CUSTOMER, UserRole.SELLER);
        assertEquals(UserRole.CUSTOMER, UserRole.CUSTOMER);
        assertEquals(UserRole.SELLER, UserRole.SELLER);
    }

    @Test
    public void testInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.valueOf("INVALID");
        });
    }
}
