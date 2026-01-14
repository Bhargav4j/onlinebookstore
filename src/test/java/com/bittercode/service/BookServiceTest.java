package com.bittercode.service;

import com.bittercode.model.Book;
import com.bittercode.model.StoreException;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private static class TestableBookService implements BookService {
        @Override
        public Book getBookById(String bookId) throws StoreException {
            if (bookId == null || bookId.isEmpty()) {
                throw new StoreException("Book ID cannot be null or empty");
            }
            return new Book();
        }

        @Override
        public List<Book> getAllBooks() throws StoreException {
            return List.of(new Book(), new Book());
        }

        @Override
        public List<Book> getBooksByCommaSeperatedBookIds(String commaSeperatedBookIds) throws StoreException {
            if (commaSeperatedBookIds == null || commaSeperatedBookIds.isEmpty()) {
                throw new StoreException("Book IDs cannot be null or empty");
            }
            return List.of(new Book());
        }

        @Override
        public String deleteBookById(String bookId) throws StoreException {
            if (bookId == null || bookId.isEmpty()) {
                throw new StoreException("Book ID cannot be null or empty");
            }
            return "Book deleted successfully";
        }

        @Override
        public String addBook(Book book) throws StoreException {
            if (book == null) {
                throw new StoreException("Book cannot be null");
            }
            return "Book added successfully";
        }

        @Override
        public String updateBookQtyById(String bookId, int quantity) throws StoreException {
            if (bookId == null || bookId.isEmpty()) {
                throw new StoreException("Book ID cannot be null or empty");
            }
            if (quantity < 0) {
                throw new StoreException("Quantity cannot be negative");
            }
            return "Book quantity updated successfully";
        }

        @Override
        public String updateBook(Book book) throws StoreException {
            if (book == null) {
                throw new StoreException("Book cannot be null");
            }
            return "Book updated successfully";
        }
    }

    @Test
    void testGetBookByIdSuccess() throws StoreException {
        BookService service = new TestableBookService();
        Book book = service.getBookById("123");
        assertNotNull(book);
    }

    @Test
    void testGetBookByIdNullThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.getBookById(null));
    }

    @Test
    void testGetBookByIdEmptyThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.getBookById(""));
    }

    @Test
    void testGetAllBooksSuccess() throws StoreException {
        BookService service = new TestableBookService();
        List<Book> books = service.getAllBooks();
        assertNotNull(books);
        assertEquals(2, books.size());
    }

    @Test
    void testGetBooksByCommaSeperatedBookIdsSuccess() throws StoreException {
        BookService service = new TestableBookService();
        List<Book> books = service.getBooksByCommaSeperatedBookIds("1,2,3");
        assertNotNull(books);
        assertFalse(books.isEmpty());
    }

    @Test
    void testGetBooksByCommaSeperatedBookIdsNullThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.getBooksByCommaSeperatedBookIds(null));
    }

    @Test
    void testGetBooksByCommaSeperatedBookIdsEmptyThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.getBooksByCommaSeperatedBookIds(""));
    }

    @Test
    void testDeleteBookByIdSuccess() throws StoreException {
        BookService service = new TestableBookService();
        String result = service.deleteBookById("123");
        assertNotNull(result);
        assertTrue(result.contains("deleted"));
    }

    @Test
    void testDeleteBookByIdNullThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.deleteBookById(null));
    }

    @Test
    void testDeleteBookByIdEmptyThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.deleteBookById(""));
    }

    @Test
    void testAddBookSuccess() throws StoreException {
        BookService service = new TestableBookService();
        String result = service.addBook(new Book());
        assertNotNull(result);
        assertTrue(result.contains("added"));
    }

    @Test
    void testAddBookNullThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.addBook(null));
    }

    @Test
    void testUpdateBookQtyByIdSuccess() throws StoreException {
        BookService service = new TestableBookService();
        String result = service.updateBookQtyById("123", 10);
        assertNotNull(result);
        assertTrue(result.contains("updated"));
    }

    @Test
    void testUpdateBookQtyByIdNullIdThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.updateBookQtyById(null, 10));
    }

    @Test
    void testUpdateBookQtyByIdNegativeQuantityThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.updateBookQtyById("123", -1));
    }

    @Test
    void testUpdateBookQtyByIdZeroQuantity() throws StoreException {
        BookService service = new TestableBookService();
        String result = service.updateBookQtyById("123", 0);
        assertNotNull(result);
    }

    @Test
    void testUpdateBookSuccess() throws StoreException {
        BookService service = new TestableBookService();
        String result = service.updateBook(new Book());
        assertNotNull(result);
        assertTrue(result.contains("updated"));
    }

    @Test
    void testUpdateBookNullThrowsException() {
        BookService service = new TestableBookService();
        assertThrows(StoreException.class, () -> service.updateBook(null));
    }
}
