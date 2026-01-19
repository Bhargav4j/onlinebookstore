package com.bittercode.service.impl;

import com.bittercode.model.Book;
import com.bittercode.model.StoreException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class BookServiceImplTest {

    @Test
    public void testGetBookById() {
        BookServiceImpl bookService = new BookServiceImpl();
        try {
            Book book = bookService.getBookById("TEST123");
            assertNotNull(book);
        } catch (StoreException e) {
            assertTrue(e.getMessage().contains("Unable to Connect to DB"));
        }
    }

    @Test
    public void testGetAllBooks() {
        BookServiceImpl bookService = new BookServiceImpl();
        try {
            List<Book> books = bookService.getAllBooks();
            assertNotNull(books);
        } catch (StoreException e) {
            assertTrue(e.getMessage().contains("Unable to Connect to DB"));
        }
    }

    @Test
    public void testAddBook() {
        BookServiceImpl bookService = new BookServiceImpl();
        try {
            Book book = new Book("TEST123", "Test Book", "Test Author", 100, 10);
            String response = bookService.addBook(book);
            assertNotNull(response);
        } catch (StoreException e) {
            assertTrue(e.getMessage().contains("Unable to Connect to DB"));
        }
    }

    @Test
    public void testUpdateBook() {
        BookServiceImpl bookService = new BookServiceImpl();
        try {
            Book book = new Book("TEST123", "Updated Book", "Updated Author", 150, 5);
            String response = bookService.updateBook(book);
            assertNotNull(response);
        } catch (StoreException e) {
            assertTrue(e.getMessage().contains("Unable to Connect to DB"));
        }
    }

    @Test
    public void testDeleteBookById() {
        BookServiceImpl bookService = new BookServiceImpl();
        try {
            String response = bookService.deleteBookById("TEST123");
            assertNotNull(response);
        } catch (StoreException e) {
            assertTrue(e.getMessage().contains("Unable to Connect to DB"));
        }
    }
}
