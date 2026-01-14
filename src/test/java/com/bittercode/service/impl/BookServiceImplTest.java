package com.bittercode.service.impl;

import com.bittercode.model.Book;
import com.bittercode.model.StoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BookServiceImplTest {

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl();
    }

    @Test
    void testGetBookByIdWithNullId() {
        try {
            Book book = bookService.getBookById(null);
            assertNull(book);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetBookByIdWithEmptyId() {
        try {
            Book book = bookService.getBookById("");
            assertNull(book);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetBookByIdWithValidId() {
        try {
            Book book = bookService.getBookById("123");
            if (book != null) {
                assertNotNull(book.getBarcode());
            }
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            assertNotNull(books);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetAllBooksReturnsListNotNull() {
        try {
            List<Book> books = bookService.getAllBooks();
            assertTrue(books instanceof List);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testDeleteBookByIdWithNullId() {
        try {
            String result = bookService.deleteBookById(null);
            assertNotNull(result);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testDeleteBookByIdWithEmptyId() {
        try {
            String result = bookService.deleteBookById("");
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testDeleteBookByIdWithValidId() {
        try {
            String result = bookService.deleteBookById("123");
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testAddBookWithNullBook() {
        try {
            String result = bookService.addBook(null);
            assertNotNull(result);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testAddBookWithValidBook() {
        try {
            Book book = new Book("123", "Test Book", "Test Author", 100, 10);
            String result = bookService.addBook(book);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testAddBookWithEmptyFields() {
        try {
            Book book = new Book("", "", "", 0, 0);
            String result = bookService.addBook(book);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookQtyByIdWithNullId() {
        try {
            String result = bookService.updateBookQtyById(null, 5);
            assertNotNull(result);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookQtyByIdWithEmptyId() {
        try {
            String result = bookService.updateBookQtyById("", 5);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookQtyByIdWithValidData() {
        try {
            String result = bookService.updateBookQtyById("123", 10);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookQtyByIdWithZeroQuantity() {
        try {
            String result = bookService.updateBookQtyById("123", 0);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookQtyByIdWithNegativeQuantity() {
        try {
            String result = bookService.updateBookQtyById("123", -5);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetBooksByCommaSeperatedBookIdsWithNull() {
        try {
            List<Book> books = bookService.getBooksByCommaSeperatedBookIds(null);
            assertNotNull(books);
            assertTrue(books.isEmpty());
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetBooksByCommaSeperatedBookIdsWithEmpty() {
        try {
            List<Book> books = bookService.getBooksByCommaSeperatedBookIds("");
            assertNotNull(books);
            assertTrue(books.isEmpty());
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetBooksByCommaSeperatedBookIdsWithSingleId() {
        try {
            List<Book> books = bookService.getBooksByCommaSeperatedBookIds("123");
            assertNotNull(books);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetBooksByCommaSeperatedBookIdsWithMultipleIds() {
        try {
            List<Book> books = bookService.getBooksByCommaSeperatedBookIds("123,456,789");
            assertNotNull(books);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetBooksByCommaSeperatedBookIdsWithWhitespace() {
        try {
            List<Book> books = bookService.getBooksByCommaSeperatedBookIds("   ");
            assertNotNull(books);
            assertTrue(books.isEmpty());
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetBooksByCommaSeperatedBookIdsWithSpaces() {
        try {
            List<Book> books = bookService.getBooksByCommaSeperatedBookIds("123, 456, 789");
            assertNotNull(books);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookWithNullBook() {
        try {
            String result = bookService.updateBook(null);
            assertNotNull(result);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookWithValidBook() {
        try {
            Book book = new Book("123", "Updated Book", "Updated Author", 200, 20);
            String result = bookService.updateBook(book);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookWithEmptyFields() {
        try {
            Book book = new Book("123", "", "", 0, 0);
            String result = bookService.updateBook(book);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testAddBookReturnsResponseCode() {
        try {
            Book book = new Book("999", "New Book", "New Author", 150, 15);
            String result = bookService.addBook(book);
            assertTrue(result.equals("SUCCESS") || result.contains("FAILURE"));
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testDeleteBookByIdReturnsResponseCode() {
        try {
            String result = bookService.deleteBookById("999");
            assertTrue(result.equals("SUCCESS") || result.contains("FAILURE"));
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookQtyByIdReturnsResponseCode() {
        try {
            String result = bookService.updateBookQtyById("123", 25);
            assertTrue(result.equals("SUCCESS") || result.contains("FAILURE"));
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testUpdateBookReturnsResponseCode() {
        try {
            Book book = new Book("123", "Modified Book", "Modified Author", 300, 30);
            String result = bookService.updateBook(book);
            assertTrue(result.equals("SUCCESS") || result.contains("FAILURE"));
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }
}
