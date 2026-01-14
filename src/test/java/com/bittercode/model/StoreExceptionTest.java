package com.bittercode.model;

import com.bittercode.constant.ResponseCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StoreExceptionTest {

    @Test
    void testConstructorWithMessageOnly() {
        String message = "Test error message";
        StoreException exception = new StoreException(message);

        assertEquals(message, exception.getErrorMessage());
        assertEquals("BAD_REQUEST", exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructorWithResponseCode() {
        StoreException exception = new StoreException(ResponseCode.SUCCESS);

        assertEquals(ResponseCode.SUCCESS.getMessage(), exception.getErrorMessage());
        assertEquals("SUCCESS", exception.getErrorCode());
        assertEquals(200, exception.getStatusCode());
    }

    @Test
    void testConstructorWithResponseCodeFailure() {
        StoreException exception = new StoreException(ResponseCode.FAILURE);

        assertEquals(ResponseCode.FAILURE.getMessage(), exception.getErrorMessage());
        assertEquals("FAILURE", exception.getErrorCode());
        assertEquals(422, exception.getStatusCode());
    }

    @Test
    void testConstructorWithResponseCodePageNotFound() {
        StoreException exception = new StoreException(ResponseCode.PAGE_NOT_FOUND);

        assertEquals(ResponseCode.PAGE_NOT_FOUND.getMessage(), exception.getErrorMessage());
        assertEquals("PAGE_NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testConstructorWithErrorCodeAndMessage() {
        String errorCode = "CUSTOM_ERROR";
        String errorMessage = "Custom error message";
        StoreException exception = new StoreException(errorCode, errorMessage);

        assertEquals(errorMessage, exception.getErrorMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(422, exception.getStatusCode());
    }

    @Test
    void testConstructorWithAllParameters() {
        int statusCode = 500;
        String errorCode = "INTERNAL_ERROR";
        String errorMessage = "Internal server error";
        StoreException exception = new StoreException(statusCode, errorCode, errorMessage);

        assertEquals(errorMessage, exception.getErrorMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(statusCode, exception.getStatusCode());
    }

    @Test
    void testSetErrorCode() {
        StoreException exception = new StoreException("Test message");
        exception.setErrorCode("NEW_ERROR_CODE");

        assertEquals("NEW_ERROR_CODE", exception.getErrorCode());
    }

    @Test
    void testSetErrorMessage() {
        StoreException exception = new StoreException("Original message");
        exception.setErrorMessage("New error message");

        assertEquals("New error message", exception.getErrorMessage());
    }

    @Test
    void testSetStatusCode() {
        StoreException exception = new StoreException("Test message");
        exception.setStatusCode(503);

        assertEquals(503, exception.getStatusCode());
    }

    @Test
    void testGetErrorCode() {
        StoreException exception = new StoreException("Test message");
        assertNotNull(exception.getErrorCode());
    }

    @Test
    void testGetErrorMessage() {
        String message = "Test error";
        StoreException exception = new StoreException(message);
        assertEquals(message, exception.getErrorMessage());
    }

    @Test
    void testGetStatusCode() {
        StoreException exception = new StoreException("Test message");
        assertTrue(exception.getStatusCode() > 0);
    }

    @Test
    void testExceptionIsThrowable() {
        StoreException exception = new StoreException("Test message");
        assertThrows(StoreException.class, () -> {
            throw exception;
        });
    }

    @Test
    void testExceptionExtendsIOException() {
        StoreException exception = new StoreException("Test message");
        assertTrue(exception instanceof java.io.IOException);
    }

    @Test
    void testConstructorWithNullMessage() {
        StoreException exception = new StoreException((String) null);
        assertNull(exception.getErrorMessage());
    }

    @Test
    void testConstructorWithEmptyMessage() {
        StoreException exception = new StoreException("");
        assertEquals("", exception.getErrorMessage());
    }

    @Test
    void testSetStatusCodeNegative() {
        StoreException exception = new StoreException("Test message");
        exception.setStatusCode(-1);
        assertEquals(-1, exception.getStatusCode());
    }

    @Test
    void testSetStatusCodeZero() {
        StoreException exception = new StoreException("Test message");
        exception.setStatusCode(0);
        assertEquals(0, exception.getStatusCode());
    }

    @Test
    void testMultipleSettersChaining() {
        StoreException exception = new StoreException("Test message");
        exception.setErrorCode("ERROR_1");
        exception.setErrorMessage("Message 1");
        exception.setStatusCode(400);

        assertEquals("ERROR_1", exception.getErrorCode());
        assertEquals("Message 1", exception.getErrorMessage());
        assertEquals(400, exception.getStatusCode());
    }
}
