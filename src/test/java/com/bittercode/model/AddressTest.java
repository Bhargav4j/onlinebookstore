package com.bittercode.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address();
    }

    @Test
    void testSetAndGetAddressLine1() {
        String addressLine1 = "123 Main Street";
        address.setAddressLine1(addressLine1);
        assertEquals(addressLine1, address.getAddressLine1());
    }

    @Test
    void testSetAndGetAddressLine2() {
        String addressLine2 = "Apt 4B";
        address.setAddressLine2(addressLine2);
        assertEquals(addressLine2, address.getAddressLine2());
    }

    @Test
    void testSetAndGetCity() {
        String city = "New York";
        address.setCity(city);
        assertEquals(city, address.getCity());
    }

    @Test
    void testSetAndGetState() {
        String state = "NY";
        address.setState(state);
        assertEquals(state, address.getState());
    }

    @Test
    void testSetAndGetCountry() {
        String country = "USA";
        address.setCountry(country);
        assertEquals(country, address.getCountry());
    }

    @Test
    void testSetAndGetPinCode() {
        long pinCode = 10001L;
        address.setPinCode(pinCode);
        assertEquals(pinCode, address.getPinCode());
    }

    @Test
    void testSetAndGetPhone() {
        String phone = "1234567890";
        address.setPhone(phone);
        assertEquals(phone, address.getPhone());
    }

    @Test
    void testAddressIsSerializable() {
        assertTrue(address instanceof java.io.Serializable);
    }

    @Test
    void testDefaultValues() {
        Address newAddress = new Address();
        assertNull(newAddress.getAddressLine1());
        assertNull(newAddress.getAddressLine2());
        assertNull(newAddress.getCity());
        assertNull(newAddress.getState());
        assertNull(newAddress.getCountry());
        assertEquals(0L, newAddress.getPinCode());
        assertNull(newAddress.getPhone());
    }

    @Test
    void testSetAddressLine1Null() {
        address.setAddressLine1(null);
        assertNull(address.getAddressLine1());
    }

    @Test
    void testSetAddressLine2Null() {
        address.setAddressLine2(null);
        assertNull(address.getAddressLine2());
    }

    @Test
    void testSetCityNull() {
        address.setCity(null);
        assertNull(address.getCity());
    }

    @Test
    void testSetStateNull() {
        address.setState(null);
        assertNull(address.getState());
    }

    @Test
    void testSetCountryNull() {
        address.setCountry(null);
        assertNull(address.getCountry());
    }

    @Test
    void testSetPhoneNull() {
        address.setPhone(null);
        assertNull(address.getPhone());
    }

    @Test
    void testSetEmptyAddressLine1() {
        address.setAddressLine1("");
        assertEquals("", address.getAddressLine1());
    }

    @Test
    void testSetEmptyAddressLine2() {
        address.setAddressLine2("");
        assertEquals("", address.getAddressLine2());
    }

    @Test
    void testSetEmptyCity() {
        address.setCity("");
        assertEquals("", address.getCity());
    }

    @Test
    void testSetEmptyState() {
        address.setState("");
        assertEquals("", address.getState());
    }

    @Test
    void testSetEmptyCountry() {
        address.setCountry("");
        assertEquals("", address.getCountry());
    }

    @Test
    void testSetEmptyPhone() {
        address.setPhone("");
        assertEquals("", address.getPhone());
    }

    @Test
    void testSetPinCodeZero() {
        address.setPinCode(0L);
        assertEquals(0L, address.getPinCode());
    }

    @Test
    void testSetPinCodeNegative() {
        address.setPinCode(-1L);
        assertEquals(-1L, address.getPinCode());
    }

    @Test
    void testSetPinCodeLargeValue() {
        address.setPinCode(999999999L);
        assertEquals(999999999L, address.getPinCode());
    }

    @Test
    void testSetPhoneWithCountryCode() {
        String phone = "+1-123-456-7890";
        address.setPhone(phone);
        assertEquals(phone, address.getPhone());
    }

    @Test
    void testSetLongAddressLine1() {
        String longAddress = "This is a very long address line that contains many characters and should still work properly";
        address.setAddressLine1(longAddress);
        assertEquals(longAddress, address.getAddressLine1());
    }

    @Test
    void testSetAllProperties() {
        address.setAddressLine1("123 Main St");
        address.setAddressLine2("Suite 100");
        address.setCity("Boston");
        address.setState("MA");
        address.setCountry("USA");
        address.setPinCode(12345L);
        address.setPhone("617-555-1234");

        assertEquals("123 Main St", address.getAddressLine1());
        assertEquals("Suite 100", address.getAddressLine2());
        assertEquals("Boston", address.getCity());
        assertEquals("MA", address.getState());
        assertEquals("USA", address.getCountry());
        assertEquals(12345L, address.getPinCode());
        assertEquals("617-555-1234", address.getPhone());
    }

    @Test
    void testMultipleSettersChaining() {
        address.setAddressLine1("456 Oak Ave");
        address.setCity("Seattle");
        address.setState("WA");

        assertEquals("456 Oak Ave", address.getAddressLine1());
        assertEquals("Seattle", address.getCity());
        assertEquals("WA", address.getState());
    }

    @Test
    void testSetPinCodeBoundaryMax() {
        address.setPinCode(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, address.getPinCode());
    }

    @Test
    void testSetPinCodeBoundaryMin() {
        address.setPinCode(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, address.getPinCode());
    }
}
