package com.bittercode.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.bittercode.constant.ResponseCode;

public class StoreExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        StoreException exception = new StoreException("Test error message");
        assertEquals("Test error message", exception.getMessage());
        assertEquals("BAD_REQUEST", exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());
        assertEquals("Test error message", exception.getErrorMessage());
    }

    @Test
    public void testConstructorWithResponseCode() {
        StoreException exception = new StoreException(ResponseCode.PAGE_NOT_FOUND);
        assertEquals(ResponseCode.PAGE_NOT_FOUND.getMessage(), exception.getMessage());
        assertEquals(ResponseCode.PAGE_NOT_FOUND.name(), exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
        assertEquals(ResponseCode.PAGE_NOT_FOUND.getMessage(), exception.getErrorMessage());
    }

    @Test
    public void testConstructorWithErrorCodeAndMessage() {
        StoreException exception = new StoreException("CUSTOM_ERROR", "Custom error message");
        assertEquals("Custom error message", exception.getMessage());
        assertEquals("CUSTOM_ERROR", exception.getErrorCode());
        assertEquals(422, exception.getStatusCode());
        assertEquals("Custom error message", exception.getErrorMessage());
    }

    @Test
    public void testConstructorWithAllParameters() {
        StoreException exception = new StoreException(503, "SERVICE_UNAVAILABLE", "Service is down");
        assertEquals("Service is down", exception.getMessage());
        assertEquals("SERVICE_UNAVAILABLE", exception.getErrorCode());
        assertEquals(503, exception.getStatusCode());
        assertEquals("Service is down", exception.getErrorMessage());
    }

    @Test
    public void testGetErrorCode() {
        StoreException exception = new StoreException("Test");
        assertEquals("BAD_REQUEST", exception.getErrorCode());
    }

    @Test
    public void testSetErrorCode() {
        StoreException exception = new StoreException("Test");
        exception.setErrorCode("NEW_CODE");
        assertEquals("NEW_CODE", exception.getErrorCode());
    }

    @Test
    public void testGetErrorMessage() {
        StoreException exception = new StoreException("Original message");
        assertEquals("Original message", exception.getErrorMessage());
    }

    @Test
    public void testSetErrorMessage() {
        StoreException exception = new StoreException("Original");
        exception.setErrorMessage("Updated message");
        assertEquals("Updated message", exception.getErrorMessage());
    }

    @Test
    public void testGetStatusCode() {
        StoreException exception = new StoreException(500, "ERROR", "Server error");
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    public void testSetStatusCode() {
        StoreException exception = new StoreException("Test");
        exception.setStatusCode(404);
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    public void testInternalServerError() {
        StoreException exception = new StoreException(ResponseCode.INTERNAL_SERVER_ERROR);
        assertEquals(500, exception.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", exception.getErrorCode());
    }

    @Test
    public void testAccessDenied() {
        StoreException exception = new StoreException(ResponseCode.ACCESS_DENIED);
        assertEquals(403, exception.getStatusCode());
        assertEquals("ACCESS_DENIED", exception.getErrorCode());
    }

    @Test
    public void testDatabaseConnectionFailure() {
        StoreException exception = new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        assertEquals(406, exception.getStatusCode());
        assertEquals("DATABASE_CONNECTION_FAILURE", exception.getErrorCode());
    }
}
