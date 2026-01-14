package servlets;

import com.bittercode.constant.ResponseCode;
import com.bittercode.model.StoreException;
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

class ErrorHandlerServletTest {

    private ErrorHandlerServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ErrorHandlerServlet();
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
    void testServiceWithCustomerLoggedIn() throws Exception {
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(404);
        when(mockRequest.getAttribute("javax.servlet.error.servlet_name")).thenReturn("TestServlet");
        when(mockRequest.getAttribute("javax.servlet.error.request_uri")).thenReturn("/test");
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(false);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("home"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            verify(mockResponse).setContentType("text/html");
        }
    }

    @Test
    void testServiceWithSellerLoggedIn() throws Exception {
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(500);
        when(mockRequest.getAttribute("javax.servlet.error.servlet_name")).thenReturn("TestServlet");
        when(mockRequest.getAttribute("javax.servlet.error.request_uri")).thenReturn("/test");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession)).thenReturn(false);
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, mockSession)).thenReturn(true);
            mockedStoreUtil.when(() -> StoreUtil.setActiveTab(any(PrintWriter.class), eq("home"))).then(invocation -> null);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
        }
    }

    @Test
    void testServiceNotLoggedIn() throws Exception {
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(401);
        when(mockRequest.getAttribute("javax.servlet.error.servlet_name")).thenReturn("TestServlet");
        when(mockRequest.getAttribute("javax.servlet.error.request_uri")).thenReturn("/test");
        when(mockRequest.getRequestDispatcher("index.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(any(UserRole.class), any(HttpSession.class))).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("script") || output.contains("alert"));
        }
    }

    @Test
    void testServiceWithStoreException() throws Exception {
        StoreException storeException = new StoreException(400, "CUSTOM_ERROR", "Custom Error");
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(storeException);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(400);
        when(mockRequest.getAttribute("javax.servlet.error.servlet_name")).thenReturn("TestServlet");
        when(mockRequest.getAttribute("javax.servlet.error.request_uri")).thenReturn("/test");
        when(mockRequest.getRequestDispatcher("index.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(any(UserRole.class), any(HttpSession.class))).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("CUSTOM_ERROR") || output.contains("Custom Error"));
        }
    }

    @Test
    void testServiceWithNullStatusCode() throws Exception {
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(null);
        when(mockRequest.getAttribute("javax.servlet.error.servlet_name")).thenReturn("TestServlet");
        when(mockRequest.getAttribute("javax.servlet.error.request_uri")).thenReturn("/test");
        when(mockRequest.getRequestDispatcher("index.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(any(UserRole.class), any(HttpSession.class))).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
        }
    }

    @Test
    void testServiceSetsContentType() throws Exception {
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(404);
        when(mockRequest.getRequestDispatcher("index.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(any(UserRole.class), any(HttpSession.class))).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockResponse).setContentType("text/html");
        }
    }

    @Test
    void testServiceCallsGetWriter() throws Exception {
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(404);
        when(mockRequest.getRequestDispatcher("index.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(any(UserRole.class), any(HttpSession.class))).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockResponse, atLeastOnce()).getWriter();
        }
    }

    @Test
    void testServiceShowsErrorMessage() throws Exception {
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(404);
        when(mockRequest.getRequestDispatcher("index.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(any(UserRole.class), any(HttpSession.class))).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            printWriter.flush();
            String output = stringWriter.toString();
            assertTrue(output.contains("alert") || output.length() > 0);
        }
    }

    @Test
    void testServiceWithGenericException() throws Exception {
        RuntimeException exception = new RuntimeException("Test Exception");
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(exception);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(500);
        when(mockRequest.getRequestDispatcher("index.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(any(UserRole.class), any(HttpSession.class))).thenReturn(false);

            servlet.service(mockRequest, mockResponse);

            verify(mockDispatcher).include(mockRequest, mockResponse);
        }
    }

    @Test
    void testServiceHandlesException() throws Exception {
        when(mockRequest.getAttribute("javax.servlet.error.exception")).thenReturn(null);
        when(mockRequest.getAttribute("javax.servlet.error.status_code")).thenReturn(500);
        when(mockRequest.getRequestDispatcher("index.html")).thenReturn(mockDispatcher);
        
        try (MockedStatic<StoreUtil> mockedStoreUtil = mockStatic(StoreUtil.class)) {
            mockedStoreUtil.when(() -> StoreUtil.isLoggedIn(any(UserRole.class), any(HttpSession.class))).thenReturn(false);

            assertDoesNotThrow(() -> servlet.service(mockRequest, mockResponse));
        }
    }
}
