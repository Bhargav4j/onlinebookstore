package servlets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

@ExtendWith(MockitoExtension.class)
public class CustomerRegisterServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    private CustomerRegisterServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new CustomerRegisterServlet();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testService_ValidRegistration() throws Exception {
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("firstname")).thenReturn("John");
        when(request.getParameter("lastname")).thenReturn("Doe");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("mailid")).thenReturn("john@test.com");

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_MissingParameters() throws Exception {
        when(request.getParameter("password")).thenReturn(null);
        when(request.getParameter("firstname")).thenReturn(null);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_InvalidPhoneNumber() throws Exception {
        when(request.getParameter("password")).thenReturn("pass");
        when(request.getParameter("firstname")).thenReturn("John");
        when(request.getParameter("lastname")).thenReturn("Doe");
        when(request.getParameter("address")).thenReturn("123 Main");
        when(request.getParameter("phone")).thenReturn("invalid");
        when(request.getParameter("mailid")).thenReturn("test@test.com");

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testConstructor() {
        CustomerRegisterServlet newServlet = new CustomerRegisterServlet();
        assertNotNull(newServlet);
    }
}
