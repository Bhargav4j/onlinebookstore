package com.bittercode.constant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookStoreConstantsTest {

    @Test
    public void testContentTypeTextHtml() {
        assertEquals("text/html", BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    }

    @Test
    public void testContentTypeTextHtmlNotNull() {
        assertNotNull(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    }

    @Test
    public void testContentTypeTextHtmlNotEmpty() {
        assertFalse(BookStoreConstants.CONTENT_TYPE_TEXT_HTML.isEmpty());
    }

    @Test
    public void testContentTypeTextHtmlValue() {
        String expectedValue = "text/html";
        assertEquals(expectedValue, BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    }
}
