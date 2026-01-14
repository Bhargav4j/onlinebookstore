package com.bittercode.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import com.bittercode.model.UserRole;
import java.io.PrintWriter;
import java.io.StringWriter;

public class StoreUtilTest {

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletRequest request;

    @Mock
    private PrintWriter printWriter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsLoggedIn_CustomerLoggedIn() {
        when(session.getAttribute("CUSTOMER")).thenReturn("customer@test.com");
        assertTrue(StoreUtil.isLoggedIn(UserRole.CUSTOMER, session));
    }

    @Test
    public void testIsLoggedIn_CustomerNotLoggedIn() {
        when(session.getAttribute("CUSTOMER")).thenReturn(null);
        assertFalse(StoreUtil.isLoggedIn(UserRole.CUSTOMER, session));
    }

    @Test
    public void testIsLoggedIn_SellerLoggedIn() {
        when(session.getAttribute("SELLER")).thenReturn("seller@test.com");
        assertTrue(StoreUtil.isLoggedIn(UserRole.SELLER, session));
    }

    @Test
    public void testIsLoggedIn_SellerNotLoggedIn() {
        when(session.getAttribute("SELLER")).thenReturn(null);
        assertFalse(StoreUtil.isLoggedIn(UserRole.SELLER, session));
    }

    @Test
    public void testSetActiveTab() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);

        StoreUtil.setActiveTab(pw, "home");

        String output = stringWriter.toString();
        assertTrue(output.contains("activeTab=home"));
        assertTrue(output.contains("classList.add"));
        assertTrue(output.contains("classList.remove"));
    }

    @Test
    public void testUpdateCartItems_AddToCart() {
        when(request.getParameter("selectedBookId")).thenReturn("BOOK123");
        when(request.getParameter("addToCart")).thenReturn("true");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn(null);
        when(session.getAttribute("qty_BOOK123")).thenReturn(null);

        StoreUtil.updateCartItems(request);

        verify(session).setAttribute("items", "BOOK123");
        verify(session).setAttribute("qty_BOOK123", 1);
    }

    @Test
    public void testUpdateCartItems_AddToCartWithExistingItems() {
        when(request.getParameter("selectedBookId")).thenReturn("BOOK456");
        when(request.getParameter("addToCart")).thenReturn("true");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn("BOOK123");
        when(session.getAttribute("qty_BOOK456")).thenReturn(null);

        StoreUtil.updateCartItems(request);

        verify(session).setAttribute("items", "BOOK123,BOOK456");
        verify(session).setAttribute("qty_BOOK456", 1);
    }

    @Test
    public void testUpdateCartItems_IncrementQuantity() {
        when(request.getParameter("selectedBookId")).thenReturn("BOOK123");
        when(request.getParameter("addToCart")).thenReturn("true");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn("BOOK123");
        when(session.getAttribute("qty_BOOK123")).thenReturn(2);

        StoreUtil.updateCartItems(request);

        verify(session).setAttribute("qty_BOOK123", 3);
    }

    @Test
    public void testUpdateCartItems_RemoveFromCart() {
        when(request.getParameter("selectedBookId")).thenReturn("BOOK123");
        when(request.getParameter("addToCart")).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn("BOOK123");
        when(session.getAttribute("qty_BOOK123")).thenReturn(2);

        StoreUtil.updateCartItems(request);

        verify(session).setAttribute("qty_BOOK123", 1);
    }

    @Test
    public void testUpdateCartItems_RemoveLastItem() {
        when(request.getParameter("selectedBookId")).thenReturn("BOOK123");
        when(request.getParameter("addToCart")).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn("BOOK123");
        when(session.getAttribute("qty_BOOK123")).thenReturn(1);

        StoreUtil.updateCartItems(request);

        verify(session).removeAttribute("qty_BOOK123");
        verify(session).setAttribute(eq("items"), anyString());
    }

    @Test
    public void testUpdateCartItems_NoSelectedBookId() {
        when(request.getParameter("selectedBookId")).thenReturn(null);
        when(request.getSession()).thenReturn(session);

        StoreUtil.updateCartItems(request);

        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    public void testUpdateCartItems_AddDuplicateItem() {
        when(request.getParameter("selectedBookId")).thenReturn("BOOK123");
        when(request.getParameter("addToCart")).thenReturn("true");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("items")).thenReturn("BOOK123");
        when(session.getAttribute("qty_BOOK123")).thenReturn(1);

        StoreUtil.updateCartItems(request);

        verify(session).setAttribute("qty_BOOK123", 2);
    }
}
