package servlets;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogoutServletTest {

    private LogoutServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;
    private UserService mockUserService;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new LogoutServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        mockDispatcher = mock(RequestDispatcher.class);
        mockUserService = mock(UserService.class);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        servlet.authService = mockUserService;
    }

    @Test
    void testDoGetSuccessfulLogout() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        when(mockUserService.logout(mockSession)).thenReturn(true);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
        verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Successfully logged out"));
    }

    @Test
    void testDoGetFailedLogout() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        when(mockUserService.logout(mockSession)).thenReturn(false);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
        verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    }

    @Test
    void testDoGetSetsContentType() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        when(mockUserService.logout(mockSession)).thenReturn(true);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    }

    @Test
    void testDoGetCallsGetWriter() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        when(mockUserService.logout(mockSession)).thenReturn(true);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse, atLeastOnce()).getWriter();
    }

    @Test
    void testDoGetCallsUserServiceLogout() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        when(mockUserService.logout(mockSession)).thenReturn(true);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockUserService).logout(mockSession);
    }

    @Test
    void testDoGetIncludesLoginPage() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        when(mockUserService.logout(mockSession)).thenReturn(true);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockRequest).getRequestDispatcher("CustomerLogin.html");
        verify(mockDispatcher).include(mockRequest, mockResponse);
    }

    @Test
    void testDoGetRetrievesSession() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        when(mockUserService.logout(mockSession)).thenReturn(true);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockRequest).getSession();
    }

    @Test
    void testDoGetShowsSuccessMessage() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        when(mockUserService.logout(mockSession)).thenReturn(true);

        servlet.doGet(mockRequest, mockResponse);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("logged out"));
    }

    @Test
    void testDoGetHandlesException() throws Exception {
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        when(mockUserService.logout(mockSession)).thenReturn(true);

        assertDoesNotThrow(() -> servlet.doGet(mockRequest, mockResponse));
    }

    @Test
    void testDoGetWithNullSession() throws Exception {
        when(mockRequest.getSession()).thenReturn(null);
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);

        assertDoesNotThrow(() -> servlet.doGet(mockRequest, mockResponse));
    }
}
