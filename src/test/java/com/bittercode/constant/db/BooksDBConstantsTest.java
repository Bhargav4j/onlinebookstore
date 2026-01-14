package com.bittercode.constant.db;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BooksDBConstantsTest {

    @Test
    void testTableBookConstant() {
        assertEquals("books", BooksDBConstants.TABLE_BOOK);
    }

    @Test
    void testColumnNameConstant() {
        assertEquals("name", BooksDBConstants.COLUMN_NAME);
    }

    @Test
    void testColumnBarcodeConstant() {
        assertEquals("barcode", BooksDBConstants.COLUMN_BARCODE);
    }

    @Test
    void testColumnAuthorConstant() {
        assertEquals("author", BooksDBConstants.COLUMN_AUTHOR);
    }

    @Test
    void testColumnPriceConstant() {
        assertEquals("price", BooksDBConstants.COLUMN_PRICE);
    }

    @Test
    void testColumnQuantityConstant() {
        assertEquals("quantity", BooksDBConstants.COLUMN_QUANTITY);
    }

    @Test
    void testAllConstantsNotNull() {
        assertNotNull(BooksDBConstants.TABLE_BOOK);
        assertNotNull(BooksDBConstants.COLUMN_NAME);
        assertNotNull(BooksDBConstants.COLUMN_BARCODE);
        assertNotNull(BooksDBConstants.COLUMN_AUTHOR);
        assertNotNull(BooksDBConstants.COLUMN_PRICE);
        assertNotNull(BooksDBConstants.COLUMN_QUANTITY);
    }

    @Test
    void testAllConstantsNotEmpty() {
        assertFalse(BooksDBConstants.TABLE_BOOK.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_NAME.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_BARCODE.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_AUTHOR.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_PRICE.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_QUANTITY.isEmpty());
    }
}
