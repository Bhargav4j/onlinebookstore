package com.bittercode.constant.db;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsersDBConstantsTest {

    @Test
    public void testTableUsers() {
        assertEquals("users", UsersDBConstants.TABLE_USERS);
    }

    @Test
    public void testColumnUsername() {
        assertEquals("username", UsersDBConstants.COLUMN_USERNAME);
    }

    @Test
    public void testColumnPassword() {
        assertEquals("password", UsersDBConstants.COLUMN_PASSWORD);
    }

    @Test
    public void testColumnFirstname() {
        assertEquals("firstname", UsersDBConstants.COLUMN_FIRSTNAME);
    }

    @Test
    public void testColumnLastname() {
        assertEquals("lastname", UsersDBConstants.COLUMN_LASTNAME);
    }

    @Test
    public void testColumnAddress() {
        assertEquals("address", UsersDBConstants.COLUMN_ADDRESS);
    }

    @Test
    public void testColumnPhone() {
        assertEquals("phone", UsersDBConstants.COLUMN_PHONE);
    }

    @Test
    public void testColumnMailid() {
        assertEquals("mailid", UsersDBConstants.COLUMN_MAILID);
    }

    @Test
    public void testColumnUsertype() {
        assertEquals("usertype", UsersDBConstants.COLUMN_USERTYPE);
    }

    @Test
    public void testAllConstantsNotNull() {
        assertNotNull(UsersDBConstants.TABLE_USERS);
        assertNotNull(UsersDBConstants.COLUMN_USERNAME);
        assertNotNull(UsersDBConstants.COLUMN_PASSWORD);
        assertNotNull(UsersDBConstants.COLUMN_FIRSTNAME);
        assertNotNull(UsersDBConstants.COLUMN_LASTNAME);
        assertNotNull(UsersDBConstants.COLUMN_ADDRESS);
        assertNotNull(UsersDBConstants.COLUMN_PHONE);
        assertNotNull(UsersDBConstants.COLUMN_MAILID);
        assertNotNull(UsersDBConstants.COLUMN_USERTYPE);
    }

    @Test
    public void testAllConstantsNotEmpty() {
        assertFalse(UsersDBConstants.TABLE_USERS.isEmpty());
        assertFalse(UsersDBConstants.COLUMN_USERNAME.isEmpty());
        assertFalse(UsersDBConstants.COLUMN_PASSWORD.isEmpty());
        assertFalse(UsersDBConstants.COLUMN_FIRSTNAME.isEmpty());
        assertFalse(UsersDBConstants.COLUMN_LASTNAME.isEmpty());
        assertFalse(UsersDBConstants.COLUMN_ADDRESS.isEmpty());
        assertFalse(UsersDBConstants.COLUMN_PHONE.isEmpty());
        assertFalse(UsersDBConstants.COLUMN_MAILID.isEmpty());
        assertFalse(UsersDBConstants.COLUMN_USERTYPE.isEmpty());
    }
}
