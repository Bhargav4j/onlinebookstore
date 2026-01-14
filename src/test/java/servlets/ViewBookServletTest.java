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
import java.io.PrintWriter;
import java.io.StringWriter;

@ExtendWith(MockitoExtension.class)
public class ViewBookServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private ViewBookServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ViewBookServlet();
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
    public void testService_LoggedIn() throws Exception {
        when(session.getAttribute("CUSTOMER")).thenReturn("customer@test.com");
        when(request.getRequestDispatcher("CustomerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testAddBookToCard_WithStock() {
        Book book = new Book("BOOK123", "Test Book", "Test Author", 29.99, 10);
        when(session.getAttribute("qty_BOOK123")).thenReturn(null);

        String card = servlet.addBookToCard(session, book);

        assertNotNull(card);
        assertTrue(card.contains("BOOK123"));
        assertTrue(card.contains("Test Book"));
        assertTrue(card.contains("Test Author"));
        assertTrue(card.contains("Add To Cart"));
    }

    @Test
    public void testAddBookToCard_OutOfStock() {
        Book book = new Book("BOOK456", "Out of Stock Book", "Author", 19.99, 0);
        when(session.getAttribute("qty_BOOK456")).thenReturn(null);

        String card = servlet.addBookToCard(session, book);

        assertNotNull(card);
        assertTrue(card.contains("Out Of Stock"));
    }

    @Test
    public void testAddBookToCard_InCart() {
        Book book = new Book("BOOK789", "Cart Book", "Author", 39.99, 20);
        when(session.getAttribute("qty_BOOK789")).thenReturn(2);

        String card = servlet.addBookToCard(session, book);

        assertNotNull(card);
        assertTrue(card.contains("BOOK789"));
    }

    @Test
    public void testConstructor() {
        ViewBookServlet newServlet = new ViewBookServlet();
        assertNotNull(newServlet);
    }
}
