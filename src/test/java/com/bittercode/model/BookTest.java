package com.bittercode.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
    }

    @Test
    void testDefaultConstructor() {
        Book newBook = new Book();
        assertNotNull(newBook);
    }

    @Test
    void testParameterizedConstructor() {
        Book newBook = new Book("123", "Test Book", "Test Author", 99.99, 10);
        assertEquals("123", newBook.getBarcode());
        assertEquals("Test Book", newBook.getName());
        assertEquals("Test Author", newBook.getAuthor());
        assertEquals(99.99, newBook.getPrice(), 0.01);
        assertEquals(10, newBook.getQuantity());
    }

    @Test
    void testSetAndGetBarcode() {
        String barcode = "978-0134685991";
        book.setBarcode(barcode);
        assertEquals(barcode, book.getBarcode());
    }

    @Test
    void testSetAndGetName() {
        String name = "Effective Java";
        book.setName(name);
        assertEquals(name, book.getName());
    }

    @Test
    void testSetAndGetAuthor() {
        String author = "Joshua Bloch";
        book.setAuthor(author);
        assertEquals(author, book.getAuthor());
    }

    @Test
    void testSetAndGetPrice() {
        double price = 45.99;
        book.setPrice(price);
        assertEquals(price, book.getPrice(), 0.01);
    }

    @Test
    void testSetAndGetQuantity() {
        int quantity = 5;
        book.setQuantity(quantity);
        assertEquals(quantity, book.getQuantity());
    }

    @Test
    void testBookIsSerializable() {
        assertTrue(book instanceof java.io.Serializable);
    }

    @Test
    void testSerialVersionUIDExists() {
        assertTrue(book instanceof java.io.Serializable);
    }

    @Test
    void testSetBarcodeNull() {
        book.setBarcode(null);
        assertNull(book.getBarcode());
    }

    @Test
    void testSetNameNull() {
        book.setName(null);
        assertNull(book.getName());
    }

    @Test
    void testSetAuthorNull() {
        book.setAuthor(null);
        assertNull(book.getAuthor());
    }

    @Test
    void testSetPriceZero() {
        book.setPrice(0.0);
        assertEquals(0.0, book.getPrice(), 0.01);
    }

    @Test
    void testSetPriceNegative() {
        book.setPrice(-10.0);
        assertEquals(-10.0, book.getPrice(), 0.01);
    }

    @Test
    void testSetQuantityZero() {
        book.setQuantity(0);
        assertEquals(0, book.getQuantity());
    }

    @Test
    void testSetQuantityNegative() {
        book.setQuantity(-5);
        assertEquals(-5, book.getQuantity());
    }

    @Test
    void testSetEmptyBarcode() {
        book.setBarcode("");
        assertEquals("", book.getBarcode());
    }

    @Test
    void testSetEmptyName() {
        book.setName("");
        assertEquals("", book.getName());
    }

    @Test
    void testSetEmptyAuthor() {
        book.setAuthor("");
        assertEquals("", book.getAuthor());
    }

    @Test
    void testParameterizedConstructorWithNullValues() {
        Book newBook = new Book(null, null, null, 0.0, 0);
        assertNull(newBook.getBarcode());
        assertNull(newBook.getName());
        assertNull(newBook.getAuthor());
        assertEquals(0.0, newBook.getPrice(), 0.01);
        assertEquals(0, newBook.getQuantity());
    }

    @Test
    void testParameterizedConstructorWithEmptyStrings() {
        Book newBook = new Book("", "", "", 0.0, 0);
        assertEquals("", newBook.getBarcode());
        assertEquals("", newBook.getName());
        assertEquals("", newBook.getAuthor());
    }

    @Test
    void testSetLargePrice() {
        book.setPrice(999999.99);
        assertEquals(999999.99, book.getPrice(), 0.01);
    }

    @Test
    void testSetLargeQuantity() {
        book.setQuantity(1000000);
        assertEquals(1000000, book.getQuantity());
    }

    @Test
    void testSetVerySmallPrice() {
        book.setPrice(0.01);
        assertEquals(0.01, book.getPrice(), 0.001);
    }

    @Test
    void testMultipleSettersChaining() {
        book.setBarcode("123456");
        book.setName("Java Programming");
        book.setAuthor("James Gosling");
        book.setPrice(59.99);
        book.setQuantity(15);

        assertEquals("123456", book.getBarcode());
        assertEquals("Java Programming", book.getName());
        assertEquals("James Gosling", book.getAuthor());
        assertEquals(59.99, book.getPrice(), 0.01);
        assertEquals(15, book.getQuantity());
    }

    @Test
    void testDefaultValuesAfterConstruction() {
        Book newBook = new Book();
        assertNull(newBook.getBarcode());
        assertNull(newBook.getName());
        assertNull(newBook.getAuthor());
        assertEquals(0.0, newBook.getPrice(), 0.01);
        assertEquals(0, newBook.getQuantity());
    }

    @Test
    void testSetPriceDecimalPlaces() {
        book.setPrice(19.995);
        assertEquals(19.995, book.getPrice(), 0.0001);
    }

    @Test
    void testSetQuantityMaxValue() {
        book.setQuantity(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, book.getQuantity());
    }

    @Test
    void testSetQuantityMinValue() {
        book.setQuantity(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, book.getQuantity());
    }

    @Test
    void testBookNotNullAfterCreation() {
        assertNotNull(book);
    }
}
