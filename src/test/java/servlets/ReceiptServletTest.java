package servlets;

import com.bittercode.constant.BookStoreConstants;
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

class ReceiptServletTest {

    private ReceiptServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;
    private BookService mockBookService;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ReceiptServlet();
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
    void testServiceLoggedInWithBooks() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        List<Book> books = new ArrayList<>();
        books.add(new Book("B001", "Test Book", "Author", 99.99, 10));
        
        when(mockBookService.getAllBooks()).thenReturn(books);
        when(mockRequest.getParameter("qty1")).thenReturn("2");
        when(mockRequest.getParameter("checked1")).thenReturn("pay");
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("order status") || output.contains("table"));
        }
    }

    @Test
    void testServiceLoggedInWithEmptyBooks() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("order status") || output.contains("Total Paid"));
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
    void testServiceShowsOrderStatus() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("order status"));
        }
    }

    @Test
    void testServiceShowsTableHeaders() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Book Code") || output.contains("Book Name"));
        }
    }

    @Test
    void testServiceWithInsufficientQuantity() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        List<Book> books = new ArrayList<>();
        books.add(new Book("B001", "Test Book", "Author", 99.99, 5));
        
        when(mockBookService.getAllBooks()).thenReturn(books);
        when(mockRequest.getParameter("qty1")).thenReturn("10");
        when(mockRequest.getParameter("checked1")).thenReturn("pay");
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Select the Qty less") || output.contains("table"));
        }
    }

    @Test
    void testServiceHandlesException() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            assertDoesNotThrow(() -> servlet.service(mockRequest, mockResponse));
        }
    }

    @Test
    void testServiceShowsTotalPaidAmount() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        when(mockBookService.getAllBooks()).thenReturn(new ArrayList<>());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Total Paid Amount"));
        }
    }
}
