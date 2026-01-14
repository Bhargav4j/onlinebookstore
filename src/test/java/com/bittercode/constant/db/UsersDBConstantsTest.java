package com.bittercode.constant.db;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsersDBConstantsTest {

    @Test
    void testTableUsersConstant() {
        assertEquals("users", UsersDBConstants.TABLE_USERS);
    }

    @Test
    void testColumnUsernameConstant() {
        assertEquals("username", UsersDBConstants.COLUMN_USERNAME);
    }

    @Test
    void testColumnPasswordConstant() {
        assertEquals("password", UsersDBConstants.COLUMN_PASSWORD);
    }

    @Test
    void testColumnFirstnameConstant() {
        assertEquals("firstname", UsersDBConstants.COLUMN_FIRSTNAME);
    }

    @Test
    void testColumnLastnameConstant() {
        assertEquals("lastname", UsersDBConstants.COLUMN_LASTNAME);
    }

    @Test
    void testColumnAddressConstant() {
        assertEquals("address", UsersDBConstants.COLUMN_ADDRESS);
    }

    @Test
    void testColumnPhoneConstant() {
        assertEquals("phone", UsersDBConstants.COLUMN_PHONE);
    }

    @Test
    void testColumnMailidConstant() {
        assertEquals("mailid", UsersDBConstants.COLUMN_MAILID);
    }

    @Test
    void testColumnUsertypeConstant() {
        assertEquals("usertype", UsersDBConstants.COLUMN_USERTYPE);
    }

    @Test
    void testAllConstantsNotNull() {
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
    void testAllConstantsNotEmpty() {
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
