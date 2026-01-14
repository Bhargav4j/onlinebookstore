package servlets;

import com.bittercode.model.Book;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.util.StoreUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewBookServletTest {

    private ViewBookServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;
    private BookService mockBookService;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ViewBookServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        mockDispatcher = mock(RequestDispatcher.class);
        mockBookService = mock(BookService.class);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        servlet.bookService = mockBookService;
    }

    @Test
    void testServiceNotLoggedIn() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            verify(mockResponse).setContentType("text/html");
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Please Login First"));
        }
    }

    @Test
    void testServiceLoggedInWithBooks() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        List<Book> books = new ArrayList<>();
        books.add(new Book("B001", "Test Book 1", "Author 1", 99.99, 10));
        books.add(new Book("B002", "Test Book 2", "Author 2", 149.99, 5));
        
        when(mockBookService.getAllBooks()).thenReturn(books);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("books"))).then(invocation -> null);
            mockedStoreUtil.when(() -> StoreUtil.updateCartItems(mockRequest)).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Available Books") || output.contains("card"));
        }
    }

    @Test
    void testServiceLoggedInWithEmptyBooks() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("books"))).then(invocation -> null);
            mockedStoreUtil.when(() -> StoreUtil.updateCartItems(mockRequest)).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Available Books") || output.contains("Proceed to Checkout"));
        }
    }

    @Test
    void testServiceSetsContentType() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockResponse).setContentType("text/html");
        }
    }

    @Test
    void testServiceCallsGetWriter() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockResponse, atLeastOnce()).getWriter();
        }
    }

    @Test
    void testServiceCallsBookServiceGetAllBooks() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);
            mockedStoreUtil.when(() -> StoreUtil.updateCartItems(mockRequest)).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockBookService).getAllBooks();
        }
    }

    @Test
    void testServiceUpdatesCartItems() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);
            mockedStoreUtil.when(() -> StoreUtil.updateCartItems(mockRequest)).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            mockedStoreUtil.verify(() -> StoreUtil.updateCartItems(mockRequest));
        }
    }

    @Test
    void testServiceShowsAvailableBooks() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);
            mockedStoreUtil.when(() -> StoreUtil.updateCartItems(mockRequest)).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Available Books"));
        }
    }

    @Test
    void testServiceHandlesException() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);
            mockedStoreUtil.when(() -> StoreUtil.updateCartItems(mockRequest)).then(invocation -> null);

            assertDoesNotThrow(() -> servlet.service(mockRequest, mockResponse));
        }
    }

    @Test
    void testAddBookToCardWithAvailableStock() {
        Book book = new Book("B001", "Test Book", "Test Author", 99.99, 10);
        when(mockSession.getAttribute("qty_B001")).thenReturn(null);
        
        String cardHtml = servlet.addBookToCard(mockSession, book);
        
        assertNotNull(cardHtml);
        assertTrue(cardHtml.contains("Test Book"));
        assertTrue(cardHtml.contains("Test Author"));
        assertTrue(cardHtml.contains("99.99"));
        assertTrue(cardHtml.contains("Add To Cart"));
    }

    @Test
    void testAddBookToCardWithItemInCart() {
        Book book = new Book("B001", "Test Book", "Test Author", 99.99, 10);
        when(mockSession.getAttribute("qty_B001")).thenReturn(2);
        
        String cardHtml = servlet.addBookToCard(mockSession, book);
        
        assertNotNull(cardHtml);
        assertTrue(cardHtml.contains("Test Book"));
        assertTrue(cardHtml.contains("glyphicon-minus") || cardHtml.contains("glyphicon-plus"));
    }

    @Test
    void testAddBookToCardWithOutOfStock() {
        Book book = new Book("B001", "Test Book", "Test Author", 99.99, 0);
        when(mockSession.getAttribute("qty_B001")).thenReturn(null);
        
        String cardHtml = servlet.addBookToCard(mockSession, book);
        
        assertNotNull(cardHtml);
        assertTrue(cardHtml.contains("Out Of Stock"));
    }

    @Test
    void testAddBookToCardShowsLowStockWarning() {
        Book book = new Book("B001", "Test Book", "Test Author", 99.99, 15);
        when(mockSession.getAttribute("qty_B001")).thenReturn(null);
        
        String cardHtml = servlet.addBookToCard(mockSession, book);
        
        assertNotNull(cardHtml);
        assertTrue(cardHtml.contains("Only 15 items left"));
    }

    @Test
    void testAddBookToCardShowsTrendingForHighStock() {
        Book book = new Book("B001", "Test Book", "Test Author", 99.99, 50);
        when(mockSession.getAttribute("qty_B001")).thenReturn(null);
        
        String cardHtml = servlet.addBookToCard(mockSession, book);
        
        assertNotNull(cardHtml);
        assertTrue(cardHtml.contains("Trending"));
    }
}
