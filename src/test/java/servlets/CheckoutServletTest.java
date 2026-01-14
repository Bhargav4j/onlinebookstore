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
public class CheckoutServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private CheckoutServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new CheckoutServlet();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testDoPost_NotLoggedIn() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn(null);
        when(request.getRequestDispatcher("CustomerLogin.html")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testDoPost_LoggedIn() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn("customer@test.com");
        when(session.getAttribute("amountToPay")).thenReturn(99.99);
        when(request.getRequestDispatcher("payment.html")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testDoPost_WithAmount() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn("customer@test.com");
        when(session.getAttribute("amountToPay")).thenReturn(150.50);
        when(request.getRequestDispatcher("payment.html")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testConstructor() {
        CheckoutServlet newServlet = new CheckoutServlet();
        assertNotNull(newServlet);
    }
}
