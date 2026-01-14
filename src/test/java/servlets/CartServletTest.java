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
import com.bittercode.model.Book;
import com.bittercode.model.Cart;
import java.io.PrintWriter;
import java.io.StringWriter;

@ExtendWith(MockitoExtension.class)
public class CartServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private CartServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new CartServlet();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testService_NotLoggedIn() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn(null);
        when(request.getRequestDispatcher("CustomerLogin.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_EmptyCart() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn("customer@test.com");
        when(session.getAttribute("items")).thenReturn(null);
        when(request.getRequestDispatcher("CustomerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testGetRowData() {
        Book book = new Book("BOOK123", "Test Book", "Test Author", 29.99, 10);
        Cart cart = new Cart(book, 2);

        String rowData = servlet.getRowData(cart);

        assertNotNull(rowData);
        assertTrue(rowData.contains("BOOK123"));
        assertTrue(rowData.contains("Test Book"));
        assertTrue(rowData.contains("Test Author"));
    }

    @Test
    public void testConstructor() {
        CartServlet newServlet = new CartServlet();
        assertNotNull(newServlet);
    }

    @Test
    public void testService_WithItems() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn("customer@test.com");
        when(session.getAttribute("items")).thenReturn("BOOK123");
        when(request.getRequestDispatcher("CustomerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }
}
