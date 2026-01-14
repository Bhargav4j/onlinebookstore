package servlets;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import com.bittercode.service.UserService;
import com.bittercode.service.impl.UserServiceImpl;
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

class CustomerLoginServletTest {

    private CustomerLoginServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;
    private UserService mockUserService;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CustomerLoginServlet();
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
    void testDoPostSuccessfulLogin() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        User mockUser = new User();
        mockUser.setFirstName("John");
        mockUser.setEmailId("customer@example.com");
        
        when(mockUserService.login(UserRole.CUSTOMER, "customer@example.com", "password123", mockSession)).thenReturn(mockUser);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
        verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Welcome") && output.contains("John"));
    }

    @Test
    void testDoPostFailedLogin() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("wrongpassword");
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(UserRole.CUSTOMER, "customer@example.com", "wrongpassword", mockSession)).thenReturn(null);

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
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(UserRole.CUSTOMER, null, "password123", mockSession)).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
    }

    @Test
    void testDoPostWithNullPassword() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn(null);
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(UserRole.CUSTOMER, "customer@example.com", null, mockSession)).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
    }

    @Test
    void testDoPostSetsContentType() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    }

    @Test
    void testDoPostCallsGetWriter() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse, atLeastOnce()).getWriter();
    }

    @Test
    void testDoPostCallsUserService() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(UserRole.CUSTOMER, "customer@example.com", "password123", mockSession)).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockUserService).login(UserRole.CUSTOMER, "customer@example.com", "password123", mockSession);
    }

    @Test
    void testDoPostIncludesCustomerHome() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        User mockUser = new User();
        mockUser.setFirstName("Jane");
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(mockUser);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockRequest).getRequestDispatcher("CustomerHome.html");
    }

    @Test
    void testDoPostHandlesException() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("CustomerLogin.html")).thenReturn(mockDispatcher);
        
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(null);

        assertDoesNotThrow(() -> servlet.doPost(mockRequest, mockResponse));
    }

    @Test
    void testDoPostShowsWelcomeMessage() throws Exception {
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("customer@example.com");
        when(mockRequest.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("password123");
        when(mockRequest.getRequestDispatcher("CustomerHome.html")).thenReturn(mockDispatcher);
        
        User mockUser = new User();
        mockUser.setFirstName("Alice");
        when(mockUserService.login(any(), any(), any(), any())).thenReturn(mockUser);

        servlet.doPost(mockRequest, mockResponse);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Happy Learning"));
    }
}
