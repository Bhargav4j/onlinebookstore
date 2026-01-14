package servlets;

import com.bittercode.constant.ResponseCode;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemoveBookServletTest {

    private RemoveBookServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;
    private BookService mockBookService;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new RemoveBookServlet();
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
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            verify(mockResponse).setContentType("text/html");
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Please Login First"));
        }
    }

    @Test
    void testServiceLoggedInWithoutBookId() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("bookId")).thenReturn(null);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("removebook"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("form") || output.contains("BookId"));
        }
    }

    @Test
    void testServiceLoggedInWithEmptyBookId() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("bookId")).thenReturn("");
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("removebook"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("form") || output.contains("Enter BookId"));
        }
    }

    @Test
    void testServiceRemoveBookSuccess() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("bookId")).thenReturn("B001");
        when(mockBookService.deleteBookById("B001")).thenReturn(ResponseCode.SUCCESS.name());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("removebook"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Book Removed Successfully"));
        }
    }

    @Test
    void testServiceRemoveBookFailure() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("bookId")).thenReturn("B999");
        when(mockBookService.deleteBookById("B999")).thenReturn("BOOK_NOT_FOUND");
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("removebook"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Book Not Available"));
        }
    }

    @Test
    void testServiceSetsContentType() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockResponse).setContentType("text/html");
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
    void testServiceCallsBookServiceDelete() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("bookId")).thenReturn("B001");
        when(mockBookService.deleteBookById("B001")).thenReturn(ResponseCode.SUCCESS.name());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockBookService).deleteBookById("B001");
        }
    }

    @Test
    void testServiceShowsRemoveBookForm() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("bookId")).thenReturn(null);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Enter BookId") || output.contains("Remove Book"));
        }
    }

    @Test
    void testServiceHandlesException() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("bookId")).thenReturn("B001");
        when(mockBookService.deleteBookById("B001")).thenThrow(new RuntimeException("Database error"));
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Failed to Remove Books"));
        }
    }

    @Test
    void testServiceTrimsBookId() throws Exception {
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("bookId")).thenReturn("  B001  ");
        when(mockBookService.deleteBookById("B001")).thenReturn(ResponseCode.SUCCESS.name());
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockBookService).deleteBookById("B001");
        }
    }
}
