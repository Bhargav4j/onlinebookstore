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
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProcessPaymentServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private ProcessPaymentServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ProcessPaymentServlet();
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
    public void testService_ProcessPayment() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn("customer@test.com");

        Book book = new Book("BOOK123", "Test Book", "Test Author", 29.99, 10);
        Cart cart = new Cart(book, 2);
        List<Cart> cartItems = new ArrayList<>();
        cartItems.add(cart);

        when(session.getAttribute("cartItems")).thenReturn(cartItems);
        when(request.getRequestDispatcher("CustomerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testAddBookToCard() {
        String card = servlet.addBookToCard("BOOK123", "Test Book", "Test Author", 29.99, 10);

        assertNotNull(card);
        assertTrue(card.contains("BOOK123"));
        assertTrue(card.contains("Test Book"));
        assertTrue(card.contains("Test Author"));
        assertTrue(card.contains("Order Placed"));
    }

    @Test
    public void testConstructor() {
        ProcessPaymentServlet newServlet = new ProcessPaymentServlet();
        assertNotNull(newServlet);
    }
}
