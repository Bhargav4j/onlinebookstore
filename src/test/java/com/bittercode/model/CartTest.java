package com.bittercode.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    private Cart cart;
    private Book book;

    @BeforeEach
    public void setUp() {
        book = new Book("ISBN123", "Test Book", "Test Author", 29.99, 10);
        cart = new Cart(book, 2);
    }

    @Test
    public void testConstructorWithParameters() {
        Book testBook = new Book("ISBN456", "Java Programming", "John Doe", 49.99, 5);
        Cart testCart = new Cart(testBook, 3);
        assertEquals(testBook, testCart.getBook());
        assertEquals(3, testCart.getQuantity());
    }

    @Test
    public void testGetBook() {
        assertEquals(book, cart.getBook());
        assertEquals("ISBN123", cart.getBook().getBarcode());
    }

    @Test
    public void testSetBook() {
        Book newBook = new Book("ISBN789", "New Book", "New Author", 19.99, 15);
        cart.setBook(newBook);
        assertEquals(newBook, cart.getBook());
        assertEquals("ISBN789", cart.getBook().getBarcode());
    }

    @Test
    public void testGetQuantity() {
        assertEquals(2, cart.getQuantity());
    }

    @Test
    public void testSetQuantity() {
        cart.setQuantity(5);
        assertEquals(5, cart.getQuantity());
    }

    @Test
    public void testSetQuantityZero() {
        cart.setQuantity(0);
        assertEquals(0, cart.getQuantity());
    }

    @Test
    public void testSetQuantityNegative() {
        cart.setQuantity(-1);
        assertEquals(-1, cart.getQuantity());
    }

    @Test
    public void testSetNullBook() {
        cart.setBook(null);
        assertNull(cart.getBook());
    }

    @Test
    public void testCartWithZeroQuantity() {
        Cart zeroCart = new Cart(book, 0);
        assertEquals(0, zeroCart.getQuantity());
        assertNotNull(zeroCart.getBook());
    }

    @Test
    public void testCartWithNullBook() {
        Cart nullBookCart = new Cart(null, 1);
        assertNull(nullBookCart.getBook());
        assertEquals(1, nullBookCart.getQuantity());
    }
}
