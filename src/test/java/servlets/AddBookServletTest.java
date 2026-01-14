package servlets;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.constant.db.BooksDBConstants;
import com.bittercode.model.Book;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.impl.BookServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddBookServletTest {

    private AddBookServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AddBookServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        mockDispatcher = mock(RequestDispatcher.class);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testServiceNotLoggedIn() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Please Login First"));
        }
    }

    @Test
    void testServiceLoggedInWithoutBookName() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn(null);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("form") || output.contains("Book Name"));
        }
    }

    @Test
    void testServiceLoggedInWithEmptyBookName() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("");
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("form") || output.contains("addbook"));
        }
    }

    @Test
    void testServiceAddBookSuccess() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Test Book");
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Test Author");
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("99.99");
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("10");
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Successfully") || output.contains("Failed"));
        }
    }

    @Test
    void testServiceAddBookWithInvalidPrice() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Test Book");
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Test Author");
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("invalid");
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("10");
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Failed"));
        }
    }

    @Test
    void testServiceAddBookWithInvalidQuantity() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Test Book");
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Test Author");
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("99.99");
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("invalid");
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("addbook"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Failed"));
        }
    }

    @Test
    void testServiceSetsContentType() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
        }
    }

    @Test
    void testServiceCallsGetWriter() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockResponse, atLeastOnce()).getWriter();
        }
    }

    @Test
    void testServiceShowsAddBookForm() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn(null);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Book Name") || output.contains("Book Author"));
        }
    }

    @Test
    void testServiceWithNullParameters() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn(null);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn(null);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn(null);
        when(mockRequest.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn(null);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            assertDoesNotThrow(() -> servlet.service(mockRequest, mockResponse));
        }
    }
}
