package com.bittercode.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void testCustomerEnumExists() {
        UserRole role = UserRole.CUSTOMER;
        assertNotNull(role);
        assertEquals("CUSTOMER", role.toString());
    }

    @Test
    void testSellerEnumExists() {
        UserRole role = UserRole.SELLER;
        assertNotNull(role);
        assertEquals("SELLER", role.toString());
    }

    @Test
    void testEnumValuesCount() {
        UserRole[] roles = UserRole.values();
        assertEquals(2, roles.length);
    }

    @Test
    void testEnumValuesContainCustomer() {
        UserRole[] roles = UserRole.values();
        boolean containsCustomer = false;
        for (UserRole role : roles) {
            if (role == UserRole.CUSTOMER) {
                containsCustomer = true;
                break;
            }
        }
        assertTrue(containsCustomer);
    }

    @Test
    void testEnumValuesContainSeller() {
        UserRole[] roles = UserRole.values();
        boolean containsSeller = false;
        for (UserRole role : roles) {
            if (role == UserRole.SELLER) {
                containsSeller = true;
                break;
            }
        }
        assertTrue(containsSeller);
    }

    @Test
    void testValueOfCustomer() {
        UserRole role = UserRole.valueOf("CUSTOMER");
        assertEquals(UserRole.CUSTOMER, role);
    }

    @Test
    void testValueOfSeller() {
        UserRole role = UserRole.valueOf("SELLER");
        assertEquals(UserRole.SELLER, role);
    }

    @Test
    void testValueOfInvalidThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> UserRole.valueOf("INVALID"));
    }

    @Test
    void testValueOfNullThrowsException() {
        assertThrows(NullPointerException.class, () -> UserRole.valueOf(null));
    }

    @Test
    void testEnumEquality() {
        UserRole role1 = UserRole.CUSTOMER;
        UserRole role2 = UserRole.CUSTOMER;
        assertEquals(role1, role2);
        assertSame(role1, role2);
    }

    @Test
    void testEnumInequality() {
        UserRole customer = UserRole.CUSTOMER;
        UserRole seller = UserRole.SELLER;
        assertNotEquals(customer, seller);
    }
}
