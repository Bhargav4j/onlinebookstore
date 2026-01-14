package com.bittercode.constant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

public class ResponseCodeTest {

    @Test
    public void testSuccessCode() {
        assertEquals(200, ResponseCode.SUCCESS.getCode());
        assertEquals("OK", ResponseCode.SUCCESS.getMessage());
    }

    @Test
    public void testFailureCode() {
        assertEquals(422, ResponseCode.FAILURE.getCode());
        assertEquals("Unprocessible Entity, Failed to Process", ResponseCode.FAILURE.getMessage());
    }

    @Test
    public void testPageNotFound() {
        assertEquals(404, ResponseCode.PAGE_NOT_FOUND.getCode());
        assertEquals("The Page You are Searching For is Not available", ResponseCode.PAGE_NOT_FOUND.getMessage());
    }

    @Test
    public void testAccessDenied() {
        assertEquals(403, ResponseCode.ACCESS_DENIED.getCode());
        assertEquals("Please Login First to continue", ResponseCode.ACCESS_DENIED.getMessage());
    }

    @Test
    public void testBadRequest() {
        assertEquals(400, ResponseCode.BAD_REQUEST.getCode());
        assertEquals("Bad Request, Please Try Again", ResponseCode.BAD_REQUEST.getMessage());
    }

    @Test
    public void testInternalServerError() {
        assertEquals(500, ResponseCode.INTERNAL_SERVER_ERROR.getCode());
        assertEquals("Internal Server Error, Try Again!!", ResponseCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    @Test
    public void testDatabaseConnectionFailure() {
        assertEquals(406, ResponseCode.DATABASE_CONNECTION_FAILURE.getCode());
        assertTrue(ResponseCode.DATABASE_CONNECTION_FAILURE.getMessage().contains("Unable to Connect to DB"));
    }

    @Test
    public void testMethodNotAllowed() {
        assertEquals(405, ResponseCode.METHOD_NOT_ALLOWED.getCode());
        assertTrue(ResponseCode.METHOD_NOT_ALLOWED.getMessage().contains("not supported"));
    }

    @Test
    public void testGetMessageByStatusCode_Success() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(200);
        assertTrue(result.isPresent());
        assertEquals(ResponseCode.SUCCESS, result.get());
    }

    @Test
    public void testGetMessageByStatusCode_NotFound() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(404);
        assertTrue(result.isPresent());
        assertEquals(ResponseCode.PAGE_NOT_FOUND, result.get());
    }

    @Test
    public void testGetMessageByStatusCode_Invalid() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(999);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetMessageByStatusCode_InternalServerError() {
        Optional<ResponseCode> result = ResponseCode.getMessageByStatusCode(500);
        assertTrue(result.isPresent());
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, result.get());
    }

    @Test
    public void testAllResponseCodesHaveValidCodes() {
        for (ResponseCode code : ResponseCode.values()) {
            assertTrue(code.getCode() >= 200 && code.getCode() < 600);
            assertNotNull(code.getMessage());
            assertFalse(code.getMessage().isEmpty());
        }
    }

    @Test
    public void testResponseCodeName() {
        assertEquals("SUCCESS", ResponseCode.SUCCESS.name());
        assertEquals("FAILURE", ResponseCode.FAILURE.name());
        assertEquals("PAGE_NOT_FOUND", ResponseCode.PAGE_NOT_FOUND.name());
    }
}
