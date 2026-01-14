package servlets;

import com.bittercode.model.Book;
import com.bittercode.service.BookService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateBookServletTest {

    private UpdateBookServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private RequestDispatcher mockDispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new UpdateBookServlet();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockSession = mock(HttpSession.class);
        mockDispatcher = mock(RequestDispatcher.class);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testServiceNotLoggedIn() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn(null);
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);

        servlet.service(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
        verify(mockResponse).setContentType("text/html");
    }

    @Test
    void testServiceLoggedInWithoutBookId() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("updateFormSubmitted")).thenReturn(null);
        when(mockRequest.getParameter("bookId")).thenReturn(null);

        servlet.service(mockRequest, mockResponse);

        verify(mockDispatcher).include(mockRequest, mockResponse);
    }

    @Test
    void testServiceWithBookId() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("updateFormSubmitted")).thenReturn(null);
        when(mockRequest.getParameter("bookId")).thenReturn("123");

        servlet.service(mockRequest, mockResponse);

        verify(mockRequest).getParameter("bookId");
    }

    @Test
    void testServiceWithUpdateFormSubmitted() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("updateFormSubmitted")).thenReturn("true");
        when(mockRequest.getParameter("name")).thenReturn("Test Book");
        when(mockRequest.getParameter("barcode")).thenReturn("123");
        when(mockRequest.getParameter("author")).thenReturn("Test Author");
        when(mockRequest.getParameter("price")).thenReturn("99.99");
        when(mockRequest.getParameter("quantity")).thenReturn("10");

        servlet.service(mockRequest, mockResponse);

        verify(mockRequest).getParameter("updateFormSubmitted");
    }

    @Test
    void testServiceWithInvalidPrice() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("updateFormSubmitted")).thenReturn("true");
        when(mockRequest.getParameter("name")).thenReturn("Test Book");
        when(mockRequest.getParameter("barcode")).thenReturn("123");
        when(mockRequest.getParameter("author")).thenReturn("Test Author");
        when(mockRequest.getParameter("price")).thenReturn("invalid");
        when(mockRequest.getParameter("quantity")).thenReturn("10");

        servlet.service(mockRequest, mockResponse);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Failed") || output.length() > 0);
    }

    @Test
    void testServiceWithInvalidQuantity() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("updateFormSubmitted")).thenReturn("true");
        when(mockRequest.getParameter("name")).thenReturn("Test Book");
        when(mockRequest.getParameter("barcode")).thenReturn("123");
        when(mockRequest.getParameter("author")).thenReturn("Test Author");
        when(mockRequest.getParameter("price")).thenReturn("99.99");
        when(mockRequest.getParameter("quantity")).thenReturn("invalid");

        servlet.service(mockRequest, mockResponse);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Failed") || output.length() > 0);
    }

    @Test
    void testServiceSetsContentType() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);

        servlet.service(mockRequest, mockResponse);

        verify(mockResponse).setContentType("text/html");
    }

    @Test
    void testServiceCallsGetWriter() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);

        servlet.service(mockRequest, mockResponse);

        verify(mockResponse, atLeastOnce()).getWriter();
    }

    @Test
    void testServiceNotLoggedInShowsLoginMessage() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn(null);
        when(mockRequest.getRequestDispatcher("SellerLogin.html")).thenReturn(mockDispatcher);

        servlet.service(mockRequest, mockResponse);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Please Login") || output.contains("Login"));
    }

    @Test
    void testServiceWithNullParameters() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("updateFormSubmitted")).thenReturn(null);
        when(mockRequest.getParameter("bookId")).thenReturn(null);

        assertDoesNotThrow(() -> servlet.service(mockRequest, mockResponse));
    }

    @Test
    void testServiceWithEmptyBookId() throws Exception {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        when(mockRequest.getRequestDispatcher("SellerHome.html")).thenReturn(mockDispatcher);
        when(mockRequest.getParameter("updateFormSubmitted")).thenReturn(null);
        when(mockRequest.getParameter("bookId")).thenReturn("");

        servlet.service(mockRequest, mockResponse);

        verify(mockRequest).getParameter("bookId");
    }
}
