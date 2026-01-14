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
public class AddBookServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private AddBookServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new AddBookServlet();
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
    public void testService_ShowAddBookForm() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getParameter("name")).thenReturn(null);
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_AddBook() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getParameter("name")).thenReturn("New Book");
        when(request.getParameter("author")).thenReturn("New Author");
        when(request.getParameter("price")).thenReturn("29");
        when(request.getParameter("quantity")).thenReturn("10");
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_AddBookWithBlankName() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getParameter("name")).thenReturn("");
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(dispatcher).include(request, response);
    }

    @Test
    public void testConstructor() {
        AddBookServlet newServlet = new AddBookServlet();
        assertNotNull(newServlet);
    }
}
