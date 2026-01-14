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
import com.bittercode.model.StoreException;
import java.io.PrintWriter;
import java.io.StringWriter;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private ErrorHandlerServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ErrorHandlerServlet();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testService_CustomerLoggedIn() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn("customer@test.com");
        when(request.getAttribute("jakarta.servlet.error.exception")).thenReturn(null);
        when(request.getAttribute("jakarta.servlet.error.status_code")).thenReturn(404);
        when(request.getRequestDispatcher("CustomerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_SellerLoggedIn() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn(null);
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getAttribute("jakarta.servlet.error.exception")).thenReturn(null);
        when(request.getAttribute("jakarta.servlet.error.status_code")).thenReturn(500);
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_NotLoggedIn() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn(null);
        when(session.getAttribute("SELLER")).thenReturn(null);
        when(request.getAttribute("jakarta.servlet.error.exception")).thenReturn(null);
        when(request.getAttribute("jakarta.servlet.error.status_code")).thenReturn(403);
        when(request.getRequestDispatcher("index.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_WithStoreException() throws Exception {
        StoreException exception = new StoreException("Test error");
        when(session.getAttribute("CUSTOMER")).thenReturn(null);
        when(session.getAttribute("SELLER")).thenReturn(null);
        when(request.getAttribute("jakarta.servlet.error.exception")).thenReturn(exception);
        when(request.getAttribute("jakarta.servlet.error.status_code")).thenReturn(400);
        when(request.getRequestDispatcher("index.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_NullStatusCode() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn("customer@test.com");
        when(request.getAttribute("jakarta.servlet.error.exception")).thenReturn(null);
        when(request.getAttribute("jakarta.servlet.error.status_code")).thenReturn(null);
        when(request.getRequestDispatcher("CustomerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testConstructor() {
        ErrorHandlerServlet newServlet = new ErrorHandlerServlet();
        assertNotNull(newServlet);
    }
}
