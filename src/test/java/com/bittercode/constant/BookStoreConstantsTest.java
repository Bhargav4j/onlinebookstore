package com.bittercode.constant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookStoreConstantsTest {

    @Test
    void testContentTypeTextHtmlConstant() {
        assertEquals("text/html", BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    }

    @Test
    void testContentTypeNotNull() {
        assertNotNull(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    }

    @Test
    void testContentTypeNotEmpty() {
        assertFalse(BookStoreConstants.CONTENT_TYPE_TEXT_HTML.isEmpty());
    }

    @Test
    void testContentTypeFormat() {
        assertTrue(BookStoreConstants.CONTENT_TYPE_TEXT_HTML.contains("/"));
    }

    @Test
    void testContentTypeStartsWithText() {
        assertTrue(BookStoreConstants.CONTENT_TYPE_TEXT_HTML.startsWith("text"));
    }
}
