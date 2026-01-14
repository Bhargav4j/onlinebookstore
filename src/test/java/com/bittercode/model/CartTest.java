package com.bittercode.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Book testBook;
    private Cart cart;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        cart = new Cart(testBook, 5);
    }

    @Test
    void testConstructorWithBookAndQuantity() {
        Book book = new Book();
        Cart newCart = new Cart(book, 3);

        assertEquals(book, newCart.getBook());
        assertEquals(3, newCart.getQuantity());
    }

    @Test
    void testConstructorWithNullBook() {
        Cart newCart = new Cart(null, 1);
        assertNull(newCart.getBook());
        assertEquals(1, newCart.getQuantity());
    }

    @Test
    void testConstructorWithZeroQuantity() {
        Book book = new Book();
        Cart newCart = new Cart(book, 0);
        assertEquals(0, newCart.getQuantity());
    }

    @Test
    void testConstructorWithNegativeQuantity() {
        Book book = new Book();
        Cart newCart = new Cart(book, -1);
        assertEquals(-1, newCart.getQuantity());
    }

    @Test
    void testGetBook() {
        assertEquals(testBook, cart.getBook());
    }

    @Test
    void testSetBook() {
        Book newBook = new Book();
        cart.setBook(newBook);
        assertEquals(newBook, cart.getBook());
    }

    @Test
    void testSetBookNull() {
        cart.setBook(null);
        assertNull(cart.getBook());
    }

    @Test
    void testGetQuantity() {
        assertEquals(5, cart.getQuantity());
    }

    @Test
    void testSetQuantity() {
        cart.setQuantity(10);
        assertEquals(10, cart.getQuantity());
    }

    @Test
    void testSetQuantityZero() {
        cart.setQuantity(0);
        assertEquals(0, cart.getQuantity());
    }

    @Test
    void testSetQuantityNegative() {
        cart.setQuantity(-5);
        assertEquals(-5, cart.getQuantity());
    }

    @Test
    void testSetQuantityLargeValue() {
        cart.setQuantity(1000000);
        assertEquals(1000000, cart.getQuantity());
    }

    @Test
    void testCartIsSerializable() {
        assertTrue(cart instanceof java.io.Serializable);
    }

    @Test
    void testUpdateBookAndQuantity() {
        Book newBook = new Book();
        cart.setBook(newBook);
        cart.setQuantity(20);

        assertEquals(newBook, cart.getBook());
        assertEquals(20, cart.getQuantity());
    }

    @Test
    void testConstructorWithLargeQuantity() {
        Book book = new Book();
        Cart newCart = new Cart(book, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, newCart.getQuantity());
    }

    @Test
    void testConstructorWithMinQuantity() {
        Book book = new Book();
        Cart newCart = new Cart(book, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, newCart.getQuantity());
    }

    @Test
    void testSetQuantityMultipleTimes() {
        cart.setQuantity(5);
        assertEquals(5, cart.getQuantity());
        cart.setQuantity(10);
        assertEquals(10, cart.getQuantity());
        cart.setQuantity(15);
        assertEquals(15, cart.getQuantity());
    }

    @Test
    void testSetBookMultipleTimes() {
        Book book1 = new Book();
        Book book2 = new Book();
        Book book3 = new Book();

        cart.setBook(book1);
        assertEquals(book1, cart.getBook());
        cart.setBook(book2);
        assertEquals(book2, cart.getBook());
        cart.setBook(book3);
        assertEquals(book3, cart.getBook());
    }

    @Test
    void testCartNotNull() {
        assertNotNull(cart);
    }

    @Test
    void testCartBookNotNullAfterConstruction() {
        assertNotNull(cart.getBook());
    }

    @Test
    void testCartQuantityPositiveAfterConstruction() {
        assertTrue(cart.getQuantity() > 0);
    }
}
