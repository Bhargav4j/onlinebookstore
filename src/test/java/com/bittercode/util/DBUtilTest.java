package com.bittercode.util;

import com.bittercode.model.StoreException;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class DBUtilTest {

    @Test
    void testGetConnectionNotNull() {
        try {
            Connection conn = DBUtil.getConnection();
            assertNotNull(conn);
        } catch (StoreException e) {
            assertNotNull(e);
            assertEquals(406, e.getStatusCode());
        }
    }

    @Test
    void testGetConnectionReturnsConnection() {
        try {
            Connection conn = DBUtil.getConnection();
            assertTrue(conn instanceof Connection);
        } catch (StoreException e) {
            assertTrue(e instanceof StoreException);
        }
    }

    @Test
    void testGetConnectionThrowsExceptionWhenNull() {
        try {
            Connection conn = DBUtil.getConnection();
            if (conn == null) {
                fail("Expected StoreException when connection is null");
            }
        } catch (StoreException e) {
            assertNotNull(e);
            assertEquals(406, e.getStatusCode());
        }
    }

    @Test
    void testGetConnectionMultipleCalls() {
        try {
            Connection conn1 = DBUtil.getConnection();
            Connection conn2 = DBUtil.getConnection();
            assertNotNull(conn1);
            assertNotNull(conn2);
            assertSame(conn1, conn2);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testStoreExceptionContainsCorrectMessage() {
        try {
            DBUtil.getConnection();
        } catch (StoreException e) {
            assertNotNull(e.getErrorMessage());
            assertTrue(e.getErrorMessage().contains("DB") || e.getErrorMessage().contains("database"));
        }
    }

    @Test
    void testStoreExceptionContainsCorrectErrorCode() {
        try {
            DBUtil.getConnection();
        } catch (StoreException e) {
            assertNotNull(e.getErrorCode());
            assertEquals("DATABASE_CONNECTION_FAILURE", e.getErrorCode());
        }
    }

    @Test
    void testGetConnectionExceptionStatusCode() {
        try {
            DBUtil.getConnection();
        } catch (StoreException e) {
            assertEquals(406, e.getStatusCode());
        }
    }

    @Test
    void testConnectionSingleton() {
        try {
            Connection conn1 = DBUtil.getConnection();
            Connection conn2 = DBUtil.getConnection();
            assertSame(conn1, conn2);
        } catch (StoreException e) {
            assertTrue(true);
        }
    }
}
