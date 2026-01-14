package com.bittercode.constant.db;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BooksDBConstantsTest {

    @Test
    public void testTableBook() {
        assertEquals("books", BooksDBConstants.TABLE_BOOK);
    }

    @Test
    public void testColumnName() {
        assertEquals("name", BooksDBConstants.COLUMN_NAME);
    }

    @Test
    public void testColumnBarcode() {
        assertEquals("barcode", BooksDBConstants.COLUMN_BARCODE);
    }

    @Test
    public void testColumnAuthor() {
        assertEquals("author", BooksDBConstants.COLUMN_AUTHOR);
    }

    @Test
    public void testColumnPrice() {
        assertEquals("price", BooksDBConstants.COLUMN_PRICE);
    }

    @Test
    public void testColumnQuantity() {
        assertEquals("quantity", BooksDBConstants.COLUMN_QUANTITY);
    }

    @Test
    public void testAllConstantsNotNull() {
        assertNotNull(BooksDBConstants.TABLE_BOOK);
        assertNotNull(BooksDBConstants.COLUMN_NAME);
        assertNotNull(BooksDBConstants.COLUMN_BARCODE);
        assertNotNull(BooksDBConstants.COLUMN_AUTHOR);
        assertNotNull(BooksDBConstants.COLUMN_PRICE);
        assertNotNull(BooksDBConstants.COLUMN_QUANTITY);
    }

    @Test
    public void testAllConstantsNotEmpty() {
        assertFalse(BooksDBConstants.TABLE_BOOK.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_NAME.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_BARCODE.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_AUTHOR.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_PRICE.isEmpty());
        assertFalse(BooksDBConstants.COLUMN_QUANTITY.isEmpty());
    }
}
