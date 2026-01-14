package servlets;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.model.Book;
import com.bittercode.model.Cart;
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

class ProcessPaymentServletTest {

    private ProcessPaymentServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;
    private BookService mockBookService;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ProcessPaymentServlet();
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
            verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Please Login First"));
        }
    }

    @Test
    void testServiceLoggedInWithCartItems() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        List<Cart> cartItems = new ArrayList<>();
        Book book1 = new Book("B001", "Test Book 1", "Author 1", 99.99, 10);
        cartItems.add(new Cart(book1, 2));
        
        when(mockSession.getAttribute("cartItems")).thenReturn(cartItems);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            verify(mockBookService).updateBookQtyById("B001", 8);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Your Orders") || output.contains("Order"));
        }
    }

    @Test
    void testServiceLoggedInWithNullCartItems() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockSession.getAttribute("cartItems")).thenReturn(null);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).then(invocation -> null);

            assertDoesNotThrow(() -> servlet.service(mockRequest, mockResponse));
        }
    }

    @Test
    void testServiceSetsContentType() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
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
    void testServiceClearsSessionAttributes() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        List<Cart> cartItems = new ArrayList<>();
        Book book1 = new Book("B001", "Test Book", "Author", 99.99, 10);
        cartItems.add(new Cart(book1, 1));
        
        when(mockSession.getAttribute("cartItems")).thenReturn(cartItems);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockSession).removeAttribute("qty_B001");
            verify(mockSession).removeAttribute("amountToPay");
            verify(mockSession).removeAttribute("cartItems");
            verify(mockSession).removeAttribute("items");
            verify(mockSession).removeAttribute("selectedBookId");
        }
    }

    @Test
    void testServiceUpdatesBookQuantity() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        List<Cart> cartItems = new ArrayList<>();
        Book book1 = new Book("B001", "Test Book", "Author", 99.99, 10);
        cartItems.add(new Cart(book1, 3));
        
        when(mockSession.getAttribute("cartItems")).thenReturn(cartItems);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockBookService).updateBookQtyById("B001", 7);
        }
    }

    @Test
    void testServiceShowsYourOrders() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        List<Cart> cartItems = new ArrayList<>();
        Book book1 = new Book("B001", "Test Book", "Author", 99.99, 10);
        cartItems.add(new Cart(book1, 1));
        
        when(mockSession.getAttribute("cartItems")).thenReturn(cartItems);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Your Orders"));
        }
    }

    @Test
    void testServiceHandlesException() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockSession.getAttribute("cartItems")).thenReturn(new ArrayList<Cart>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            assertDoesNotThrow(() -> servlet.service(mockRequest, mockResponse));
        }
    }

    @Test
    void testAddBookToCardGeneratesCorrectHTML() {
        String html = servlet.addBookToCard("B001", "Test Book", "Test Author", 99.99, 5);
        
        assertNotNull(html);
        assertTrue(html.contains("Test Book"));
        assertTrue(html.contains("Test Author"));
        assertTrue(html.contains("99.99"));
        assertTrue(html.contains("Order Placed"));
    }
}
