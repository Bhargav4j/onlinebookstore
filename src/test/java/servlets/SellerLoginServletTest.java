package servlets;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
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

class SellerLoginServletTest {

    private SellerLoginServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;
    private UserService mockUserService;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new SellerLoginServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        mockDispatcher = mock(RequestDispatcher.class);
        mockUserService = mock(UserService.class);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        servlet.userService = mockUserService;
    }

    @Test
    void testDoPostSuccessfulLogin() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        
        User mockUser = new User();
        mockUser.setFirstName("Bob");
        mockUser.setEmailId("seller@example.com");
        
        when(mockUserService.login(UserRole.SELLER, "seller@example.com", "password123", mockSession)).thenReturn(mockUser);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
        verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Welcome") && output.contains("Bob"));
    }

    @Test
    void testDoPostFailedLogin() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("wrongpassword");
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(UserRole.SELLER, "seller@example.com", "wrongpassword", mockSession)).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Incorrect UserName or PassWord"));
    }

    @Test
    void testDoPostWithNullUsername() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn(null);
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(UserRole.SELLER, null, "password123", mockSession)).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
    }

    @Test
    void testDoPostWithNullPassword() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn(null);
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(UserRole.SELLER, "seller@example.com", null, mockSession)).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
    }

    @Test
    void testDoPostSetsContentType() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    }

    @Test
    void testDoPostCallsGetWriter() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse, atLeastOnce()).getWriter();
    }

    @Test
    void testDoPostCallsUserService() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(UserRole.SELLER, "seller@example.com", "password123", mockSession)).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockUserService).login(UserRole.SELLER, "seller@example.com", "password123", mockSession);
    }

    @Test
    void testDoPostIncludesSellerHome() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        
        User mockUser = new User();
        mockUser.setFirstName("Alice");
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(mockUser);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockRequest).getRequestDispatcher("SellerHome.html");
    }

    @Test
    void testDoPostHandlesException() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(null);

        assertDoesNotThrow(() -> servlet.doPost(mockRequest, mockResponse));
    }

    @Test
    void testDoPostShowsWelcomeMessage() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("seller@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        
        User mockUser = new User();
        mockUser.setFirstName("Charlie");
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(mockUser);

        servlet.doPost(mockRequest, mockResponse);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Happy Learning"));
    }
}
