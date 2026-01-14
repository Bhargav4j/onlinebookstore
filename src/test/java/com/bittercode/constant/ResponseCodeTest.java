package com.bittercode.constant;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class ResponseCodeTest {

    @Test
    void testSuccessEnumValues() {
        assertEquals(200, ResponseCode.SUCCESS.getCode());
        assertEquals("OK", ResponseCode.SUCCESS.getMessage());
    }

    @Test
    void testFailureEnumValues() {
        assertEquals(422, ResponseCode.FAILURE.getCode());
        assertEquals("Unprocessible Entity, Failed to Process", ResponseCode.FAILURE.getMessage());
    }

    @Test
    void testPageNotFoundEnumValues() {
        assertEquals(404, ResponseCode.PAGE_NOT_FOUND.getCode());
        assertEquals("The Page You are Searching For is Not available", ResponseCode.PAGE_NOT_FOUND.getMessage());
    }

    @Test
    void testAccessDeniedEnumValues() {
        assertEquals(403, ResponseCode.ACCESS_DENIED.getCode());
        assertEquals("Please Login First to continue", ResponseCode.ACCESS_DENIED.getMessage());
    }

    @Test
    void testBadRequestEnumValues() {
        assertEquals(400, ResponseCode.BAD_REQUEST.getCode());
        assertEquals("Bad Request, Please Try Again", ResponseCode.BAD_REQUEST.getMessage());
    }

    @Test
    void testInternalServerErrorEnumValues() {
        assertEquals(500, ResponseCode.INTERNAL_SERVER_ERROR.getCode());
        assertEquals("Internal Server Error, Try Again!!", ResponseCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    @Test
    void testDatabaseConnectionFailureEnumValues() {
        assertEquals(406, ResponseCode.DATABASE_CONNECTION_FAILURE.getCode());
        assertEquals("Unable to Connect to DB, Please Check your db credentials in application.properties",
                     ResponseCode.DATABASE_CONNECTION_FAILURE.getMessage());
    }

    @Test
    void testMethodNotAllowedEnumValues() {
        assertEquals(405, ResponseCode.METHOD_NOT_ALLOWED.getCode());
        assertEquals("Requested HTTP method is not supported by this URL", ResponseCode.METHOD_NOT_ALLOWED.getMessage());
    }

    @Test
    void testGetMessageByStatusCodeSuccess() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(200);
        assertTrue(result.isPresent());
        assertEquals(ResponseCode.SUCCESS, result.get());
    }

    @Test
    void testGetMessageByStatusCodeFailure() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(422);
        assertTrue(result.isPresent());
        assertEquals(ResponseCode.FAILURE, result.get());
    }

    @Test
    void testGetMessageByStatusCodeNotFound() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(404);
        assertTrue(result.isPresent());
        assertEquals(ResponseCode.PAGE_NOT_FOUND, result.get());
    }

    @Test
    void testGetMessageByStatusCodeInvalidCode() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(999);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetMessageByStatusCodeAccessDenied() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(403);
        assertTrue(result.isPresent());
        assertEquals(ResponseCode.ACCESS_DENIED, result.get());
    }

    @Test
    void testGetMessageByStatusCodeInternalServerError() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(500);
        assertTrue(result.isPresent());
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, result.get());
    }

    @Test
    void testAllEnumValuesExist() {
        ResponseCode[] values = ResponseCode.values();
        assertEquals(8, values.length);
    }

    @Test
    void testEnumToString() {
        assertEquals("SUCCESS", ResponseCode.SUCCESS.toString());
        assertEquals("FAILURE", ResponseCode.FAILURE.toString());
    }
}
