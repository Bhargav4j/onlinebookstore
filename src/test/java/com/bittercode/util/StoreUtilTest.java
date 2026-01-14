package com.bittercode.util;

import com.bittercode.model.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreUtilTest {

    private HttpSession mockSession;
    private HttpServletRequest mockRequest;
    private PrintWriter printWriter;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() {
        mockSession = mock(HttpSession.class);
        mockRequest = mock(HttpServletRequest.class);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    void testIsLoggedInReturnsTrueWhenSessionAttributeExists() {
        when(mockSession.getAttribute("CUSTOMER")).thenReturn("someValue");
        assertTrue(StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession));
    }

    @Test
    void testIsLoggedInReturnsFalseWhenSessionAttributeIsNull() {
        when(mockSession.getAttribute("CUSTOMER")).thenReturn(null);
        assertFalse(StoreUtil.isLoggedIn(UserRole.CUSTOMER, mockSession));
    }

    @Test
    void testIsLoggedInForSellerRole() {
        when(mockSession.getAttribute("SELLER")).thenReturn("sellerValue");
        assertTrue(StoreUtil.isLoggedIn(UserRole.SELLER, mockSession));
    }

    @Test
    void testIsLoggedInForSellerRoleReturnsFalse() {
        when(mockSession.getAttribute("SELLER")).thenReturn(null);
        assertFalse(StoreUtil.isLoggedIn(UserRole.SELLER, mockSession));
    }

    @Test
    void testSetActiveTabWritesScript() {
        StoreUtil.setActiveTab(printWriter, "home");
        printWriter.flush();
        String output = stringWriter.toString();
        assertNotNull(output);
        assertTrue(output.contains("script"));
    }

    @Test
    void testSetActiveTabContainsActiveTab() {
        String activeTab = "products";
        StoreUtil.setActiveTab(printWriter, activeTab);
        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains(activeTab));
    }

    @Test
    void testUpdateCartItemsAddToCartNewItem() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn("book1");
        when(mockRequest.getParameter("addToCart")).thenReturn("true");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("items")).thenReturn(null);
        when(mockSession.getAttribute("qty_book1")).thenReturn(null);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession).setAttribute("items", "book1");
        verify(mockSession).setAttribute("qty_book1", 1);
    }

    @Test
    void testUpdateCartItemsAddToCartExistingItems() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn("book2");
        when(mockRequest.getParameter("addToCart")).thenReturn("true");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("items")).thenReturn("book1");
        when(mockSession.getAttribute("qty_book2")).thenReturn(null);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession).setAttribute("items", "book1,book2");
        verify(mockSession).setAttribute("qty_book2", 1);
    }

    @Test
    void testUpdateCartItemsAddToCartIncrementQuantity() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn("book1");
        when(mockRequest.getParameter("addToCart")).thenReturn("true");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("items")).thenReturn("book1");
        when(mockSession.getAttribute("qty_book1")).thenReturn(2);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession).setAttribute("qty_book1", 3);
    }

    @Test
    void testUpdateCartItemsRemoveFromCartDecrementQuantity() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn("book1");
        when(mockRequest.getParameter("addToCart")).thenReturn(null);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("items")).thenReturn("book1");
        when(mockSession.getAttribute("qty_book1")).thenReturn(3);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession).setAttribute("qty_book1", 2);
    }

    @Test
    void testUpdateCartItemsRemoveFromCartCompletelyWhenQtyOne() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn("book1");
        when(mockRequest.getParameter("addToCart")).thenReturn(null);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("items")).thenReturn("book1");
        when(mockSession.getAttribute("qty_book1")).thenReturn(1);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession).removeAttribute("qty_book1");
        verify(mockSession).setAttribute("items", "");
    }

    @Test
    void testUpdateCartItemsRemoveFromCartMiddleItem() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn("book2");
        when(mockRequest.getParameter("addToCart")).thenReturn(null);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("items")).thenReturn("book1,book2,book3");
        when(mockSession.getAttribute("qty_book2")).thenReturn(1);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession).removeAttribute("qty_book2");
    }

    @Test
    void testUpdateCartItemsNoSelectedBookId() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn(null);
        when(mockRequest.getSession()).thenReturn(mockSession);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession, never()).setAttribute(anyString(), any());
    }

    @Test
    void testUpdateCartItemsAddDuplicateBook() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn("book1");
        when(mockRequest.getParameter("addToCart")).thenReturn("true");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("items")).thenReturn("book1,book2");
        when(mockSession.getAttribute("qty_book1")).thenReturn(1);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession, never()).setAttribute(eq("items"), contains("book1,book1"));
        verify(mockSession).setAttribute("qty_book1", 2);
    }

    @Test
    void testUpdateCartItemsEmptyItemsString() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn("book1");
        when(mockRequest.getParameter("addToCart")).thenReturn("true");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("items")).thenReturn("");
        when(mockSession.getAttribute("qty_book1")).thenReturn(null);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession).setAttribute("items", "book1");
        verify(mockSession).setAttribute("qty_book1", 1);
    }

    @Test
    void testIsLoggedInWithNullSession() {
        assertThrows(NullPointerException.class, () ->
            StoreUtil.isLoggedIn(UserRole.CUSTOMER, null)
        );
    }

    @Test
    void testSetActiveTabWithNullPrintWriter() {
        assertThrows(NullPointerException.class, () ->
            StoreUtil.setActiveTab(null, "home")
        );
    }

    @Test
    void testUpdateCartItemsRemoveFromEmptyCart() {
        when(mockRequest.getParameter("selectedBookId")).thenReturn("book1");
        when(mockRequest.getParameter("addToCart")).thenReturn(null);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("items")).thenReturn("book1");
        when(mockSession.getAttribute("qty_book1")).thenReturn(null);

        StoreUtil.updateCartItems(mockRequest);

        verify(mockSession).removeAttribute("qty_book1");
    }
}
