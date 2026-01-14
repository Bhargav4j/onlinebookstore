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
import com.bittercode.service.BookService;
import java.io.PrintWriter;
import java.io.StringWriter;

@ExtendWith(MockitoExtension.class)
public class UpdateBookServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private BookService bookService;

    private UpdateBookServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new UpdateBookServlet();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testService_NotLoggedIn() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn(null);
        when(request.getRequestDispatcher("SellerLogin.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_LoggedInShowForm() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getParameter("updateFormSubmitted")).thenReturn(null);
        when(request.getParameter("bookId")).thenReturn("BOOK123");
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_UpdateBook() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getParameter("updateFormSubmitted")).thenReturn("true");
        when(request.getParameter("name")).thenReturn("Updated Book");
        when(request.getParameter("barcode")).thenReturn("BOOK123");
        when(request.getParameter("author")).thenReturn("Updated Author");
        when(request.getParameter("price")).thenReturn("29.99");
        when(request.getParameter("quantity")).thenReturn("10");
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testConstructor() {
        UpdateBookServlet newServlet = new UpdateBookServlet();
        assertNotNull(newServlet);
    }
}
