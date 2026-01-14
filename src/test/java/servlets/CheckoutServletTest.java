package servlets;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.model.UserRole;
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

class CheckoutServletTest {

    private CheckoutServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CheckoutServlet();
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
    void testDoPostNotLoggedIn() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(false);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Please Login First"));
        }
    }

    @Test
    void testDoPostLoggedInWithAmount() throws Exception {
        when(mockRequest.getRequestDispatcher("payment.html")).thenReturn(mockDispatcher);
        when(mockSession.getAttribute("amountToPay")).thenReturn(299.99);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).then(invocation -> null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("299.99") || output.contains("Total Amount"));
        }
    }

    @Test
    void testDoPostLoggedInWithNullAmount() throws Exception {
        when(mockRequest.getRequestDispatcher("payment.html")).thenReturn(mockDispatcher);
        when(mockSession.getAttribute("amountToPay")).thenReturn(null);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("cart"))).then(invocation -> null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Total Amount") || output.contains("null"));
        }
    }

    @Test
    void testDoPostSetsContentType() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(false);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
        }
    }

    @Test
    void testDoPostCallsGetWriter() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(false);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockResponse, atLeastOnce()).getWriter();
        }
    }

    @Test
    void testDoPostIncludesPaymentPage() throws Exception {
        when(mockRequest.getRequestDispatcher("payment.html")).thenReturn(mockDispatcher);
        when(mockSession.getAttribute("amountToPay")).thenReturn(199.99);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockRequest).getRequestDispatcher("payment.html");
            verify(mockDispatcher).include(mockRequest, mockResponse);
        }
    }

    @Test
    void testDoPostShowsPayButton() throws Exception {
        when(mockRequest.getRequestDispatcher("payment.html")).thenReturn(mockDispatcher);
        when(mockSession.getAttribute("amountToPay")).thenReturn(99.99);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.doPost(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Pay") || output.contains("Place Order"));
        }
    }

    @Test
    void testDoPostRetrievesAmountFromSession() throws Exception {
        when(mockRequest.getRequestDispatcher("payment.html")).thenReturn(mockDispatcher);
        when(mockSession.getAttribute("amountToPay")).thenReturn(499.99);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.doPost(mockRequest, mockResponse);

            verify(mockSession).getAttribute("amountToPay");
        }
    }

    @Test
    void testDoPostHandlesException() throws Exception {
        when(mockRequest.getRequestDispatcher("payment.html")).thenReturn(mockDispatcher);
        when(mockSession.getAttribute("amountToPay")).thenReturn(99.99);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            assertDoesNotThrow(() -> servlet.doPost(mockRequest, mockResponse));
        }
    }

    @Test
    void testDoPostShowsTotalAmount() throws Exception {
        when(mockRequest.getRequestDispatcher("payment.html")).thenReturn(mockDispatcher);
        when(mockSession.getAttribute("amountToPay")).thenReturn(599.99);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), anyString())).then(invocation -> null);

            servlet.doPost(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("Total Amount") || output.contains("599.99"));
        }
    }
}
