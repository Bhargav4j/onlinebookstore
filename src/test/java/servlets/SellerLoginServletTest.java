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
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

@ExtendWith(MockitoExtension.class)
public class SellerLoginServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private SellerLoginServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new SellerLoginServlet();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testDoPost_ValidCredentials() throws Exception {
        when(request.getParameter("username")).thenReturn("seller@test.com");
        when(request.getParameter("password")).thenReturn("password123");

        servlet.doPost(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testDoPost_InvalidCredentials() throws Exception {
        when(request.getParameter("username")).thenReturn("invalid@test.com");
        when(request.getParameter("password")).thenReturn("wrongpass");

        servlet.doPost(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testDoPost_NullUsername() throws Exception {
        when(request.getParameter("username")).thenReturn(null);
        when(request.getParameter("password")).thenReturn("password");

        servlet.doPost(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testConstructor() {
        SellerLoginServlet newServlet = new SellerLoginServlet();
        assertNotNull(newServlet);
    }
}
