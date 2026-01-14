package com.bittercode.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private Book book;

    @BeforeEach
    public void setUp() {
        book = new Book("ISBN123", "Test Book", "Test Author", 29.99, 10);
    }

    @Test
    public void testConstructorWithAllParameters() {
        Book newBook = new Book("ISBN456", "Java Programming", "John Doe", 49.99, 5);
        assertEquals("ISBN456", newBook.getBarcode());
        assertEquals("Java Programming", newBook.getName());
        assertEquals("John Doe", newBook.getAuthor());
        assertEquals(49.99, newBook.getPrice(), 0.01);
        assertEquals(5, newBook.getQuantity());
    }

    @Test
    public void testDefaultConstructor() {
        Book emptyBook = new Book();
        assertNotNull(emptyBook);
    }

    @Test
    public void testGetBarcode() {
        assertEquals("ISBN123", book.getBarcode());
    }

    @Test
    public void testSetBarcode() {
        book.setBarcode("ISBN789");
        assertEquals("ISBN789", book.getBarcode());
    }

    @Test
    public void testGetName() {
        assertEquals("Test Book", book.getName());
    }

    @Test
    public void testSetName() {
        book.setName("New Book Name");
        assertEquals("New Book Name", book.getName());
    }

    @Test
    public void testGetAuthor() {
        assertEquals("Test Author", book.getAuthor());
    }

    @Test
    public void testSetAuthor() {
        book.setAuthor("New Author");
        assertEquals("New Author", book.getAuthor());
    }

    @Test
    public void testGetPrice() {
        assertEquals(29.99, book.getPrice(), 0.01);
    }

    @Test
    public void testSetPrice() {
        book.setPrice(39.99);
        assertEquals(39.99, book.getPrice(), 0.01);
    }

    @Test
    public void testGetQuantity() {
        assertEquals(10, book.getQuantity());
    }

    @Test
    public void testSetQuantity() {
        book.setQuantity(20);
        assertEquals(20, book.getQuantity());
    }

    @Test
    public void testSetQuantityZero() {
        book.setQuantity(0);
        assertEquals(0, book.getQuantity());
    }

    @Test
    public void testSetNegativePrice() {
        book.setPrice(-10.0);
        assertEquals(-10.0, book.getPrice(), 0.01);
    }

    @Test
    public void testSetNullBarcode() {
        book.setBarcode(null);
        assertNull(book.getBarcode());
    }

    @Test
    public void testSetNullName() {
        book.setName(null);
        assertNull(book.getName());
    }

    @Test
    public void testSetNullAuthor() {
        book.setAuthor(null);
        assertNull(book.getAuthor());
    }
}
