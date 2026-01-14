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
public class StoreBookServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private StoreBookServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new StoreBookServlet();
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
    public void testService_LoggedIn() throws Exception {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        when(request.getRequestDispatcher("SellerHome.html")).thenReturn(dispatcher);

        servlet.service(request, response);

        verify(response).setContentType("text/html");
    }

    @Test
    public void testGetRowData() {
        Book book = new Book("BOOK123", "Test Book", "Test Author", 29.99, 10);

        String rowData = servlet.getRowData(book);

        assertNotNull(rowData);
        assertTrue(rowData.contains("BOOK123"));
        assertTrue(rowData.contains("Test Book"));
        assertTrue(rowData.contains("Test Author"));
        assertTrue(rowData.contains("29.99"));
    }

    @Test
    public void testConstructor() {
        StoreBookServlet newServlet = new StoreBookServlet();
        assertNotNull(newServlet);
    }
}
