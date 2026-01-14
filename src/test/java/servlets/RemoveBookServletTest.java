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
public class RemoveBookServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private RemoveBookServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new RemoveBookServlet();
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
    public void testService_ShowForm() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getParameter("bookId")).thenReturn(null);
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_RemoveBook() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getParameter("bookId")).thenReturn("BOOK123");
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testService_BlankBookId() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getParameter("bookId")).thenReturn("");
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testConstructor() {
        RemoveBookServlet newServlet = new RemoveBookServlet();
        assertNotNull(newServlet);
    }
}
