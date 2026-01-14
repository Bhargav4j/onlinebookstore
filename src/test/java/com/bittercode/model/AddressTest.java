package com.bittercode.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class AddressTest {

    private Address address;

    @BeforeEach
    public void setUp() {
        address = new Address();
    }

    @Test
    public void testGetAddressLine1() {
        address.setAddressLine1("123 Main Street");
        assertEquals("123 Main Street", address.getAddressLine1());
    }

    @Test
    public void testSetAddressLine1() {
        address.setAddressLine1("456 Oak Avenue");
        assertEquals("456 Oak Avenue", address.getAddressLine1());
    }

    @Test
    public void testGetAddressLine2() {
        address.setAddressLine2("Apt 2B");
        assertEquals("Apt 2B", address.getAddressLine2());
    }

    @Test
    public void testSetAddressLine2() {
        address.setAddressLine2("Suite 100");
        assertEquals("Suite 100", address.getAddressLine2());
    }

    @Test
    public void testGetCity() {
        address.setCity("New York");
        assertEquals("New York", address.getCity());
    }

    @Test
    public void testSetCity() {
        address.setCity("Los Angeles");
        assertEquals("Los Angeles", address.getCity());
    }

    @Test
    public void testGetState() {
        address.setState("California");
        assertEquals("California", address.getState());
    }

    @Test
    public void testSetState() {
        address.setState("Texas");
        assertEquals("Texas", address.getState());
    }

    @Test
    public void testGetCountry() {
        address.setCountry("USA");
        assertEquals("USA", address.getCountry());
    }

    @Test
    public void testSetCountry() {
        address.setCountry("Canada");
        assertEquals("Canada", address.getCountry());
    }

    @Test
    public void testGetPinCode() {
        address.setPinCode(12345);
        assertEquals(12345, address.getPinCode());
    }

    @Test
    public void testSetPinCode() {
        address.setPinCode(67890);
        assertEquals(67890, address.getPinCode());
    }

    @Test
    public void testGetPhone() {
        address.setPhone("555-1234");
        assertEquals("555-1234", address.getPhone());
    }

    @Test
    public void testSetPhone() {
        address.setPhone("555-5678");
        assertEquals("555-5678", address.getPhone());
    }

    @Test
    public void testSetNullAddressLine1() {
        address.setAddressLine1(null);
        assertNull(address.getAddressLine1());
    }

    @Test
    public void testSetNullCity() {
        address.setCity(null);
        assertNull(address.getCity());
    }

    @Test
    public void testSetNullPhone() {
        address.setPhone(null);
        assertNull(address.getPhone());
    }

    @Test
    public void testSetZeroPinCode() {
        address.setPinCode(0);
        assertEquals(0, address.getPinCode());
    }

    @Test
    public void testCompleteAddress() {
        address.setAddressLine1("789 Elm Street");
        address.setAddressLine2("Floor 3");
        address.setCity("Chicago");
        address.setState("Illinois");
        address.setCountry("USA");
        address.setPinCode(60601);
        address.setPhone("312-555-0000");

        assertEquals("789 Elm Street", address.getAddressLine1());
        assertEquals("Floor 3", address.getAddressLine2());
        assertEquals("Chicago", address.getCity());
        assertEquals("Illinois", address.getState());
        assertEquals("USA", address.getCountry());
        assertEquals(60601, address.getPinCode());
        assertEquals("312-555-0000", address.getPhone());
    }
}
