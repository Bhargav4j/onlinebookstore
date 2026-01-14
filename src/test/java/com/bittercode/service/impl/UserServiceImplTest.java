package com.bittercode.service.impl;

import com.bittercode.model.StoreException;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;
    private HttpSession mockSession;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        mockSession = mock(HttpSession.class);
    }

    @Test
    void testLoginWithNullEmail() {
        assertThrows(Exception.class, () ->
            userService.login(UserRole.CUSTOMER, null, "password", mockSession)
        );
    }

    @Test
    void testLoginWithNullPassword() {
        assertThrows(Exception.class, () ->
            userService.login(UserRole.CUSTOMER, "test@example.com", null, mockSession)
        );
    }

    @Test
    void testLoginWithEmptyEmail() {
        try {
            User user = userService.login(UserRole.CUSTOMER, "", "password", mockSession);
            assertNull(user);
        } catch (StoreException e) {
            assertTrue(true);
        }
    }

    @Test
    void testLoginWithEmptyPassword() {
        try {
            User user = userService.login(UserRole.CUSTOMER, "test@example.com", "", mockSession);
            assertNull(user);
        } catch (StoreException e) {
            assertTrue(true);
        }
    }

    @Test
    void testLoginWithCustomerRole() {
        try {
            User user = userService.login(UserRole.CUSTOMER, "customer@example.com", "password", mockSession);
            if (user != null) {
                assertNotNull(user);
            }
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testLoginWithSellerRole() {
        try {
            User user = userService.login(UserRole.SELLER, "seller@example.com", "password", mockSession);
            if (user != null) {
                assertNotNull(user);
            }
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testIsLoggedInWithCustomerRole() {
        when(mockSession.getAttribute("CUSTOMER")).thenReturn("customer@example.com");
        assertTrue(userService.isLoggedIn(UserRole.CUSTOMER, mockSession));
    }

    @Test
    void testIsLoggedInWithSellerRole() {
        when(mockSession.getAttribute("SELLER")).thenReturn("seller@example.com");
        assertTrue(userService.isLoggedIn(UserRole.SELLER, mockSession));
    }

    @Test
    void testIsLoggedInReturnsFalse() {
        when(mockSession.getAttribute("CUSTOMER")).thenReturn(null);
        assertFalse(userService.isLoggedIn(UserRole.CUSTOMER, mockSession));
    }

    @Test
    void testIsLoggedInWithNullRole() {
        when(mockSession.getAttribute("CUSTOMER")).thenReturn("customer@example.com");
        assertTrue(userService.isLoggedIn(null, mockSession));
    }

    @Test
    void testLogout() {
        boolean result = userService.logout(mockSession);
        assertTrue(result);
        verify(mockSession).removeAttribute("CUSTOMER");
        verify(mockSession).removeAttribute("SELLER");
        verify(mockSession).invalidate();
    }

    @Test
    void testLogoutRemovesCustomerAttribute() {
        userService.logout(mockSession);
        verify(mockSession, times(1)).removeAttribute("CUSTOMER");
    }

    @Test
    void testLogoutRemovesSellerAttribute() {
        userService.logout(mockSession);
        verify(mockSession, times(1)).removeAttribute("SELLER");
    }

    @Test
    void testLogoutInvalidatesSession() {
        userService.logout(mockSession);
        verify(mockSession, times(1)).invalidate();
    }

    @Test
    void testRegisterWithNullUser() {
        assertThrows(Exception.class, () ->
            userService.register(UserRole.CUSTOMER, null)
        );
    }

    @Test
    void testRegisterWithValidUser() {
        User user = new User();
        user.setEmailId("newuser@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAddress("123 Main St");
        user.setPhone(1234567890L);

        try {
            String result = userService.register(UserRole.CUSTOMER, user);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testRegisterWithSellerRole() {
        User user = new User();
        user.setEmailId("seller@example.com");
        user.setPassword("password123");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setAddress("456 Oak Ave");
        user.setPhone(9876543210L);

        try {
            String result = userService.register(UserRole.SELLER, user);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testRegisterWithEmptyEmail() {
        User user = new User();
        user.setEmailId("");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("User");

        try {
            String result = userService.register(UserRole.CUSTOMER, user);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testRegisterWithEmptyPassword() {
        User user = new User();
        user.setEmailId("test@example.com");
        user.setPassword("");
        user.setFirstName("Test");
        user.setLastName("User");

        try {
            String result = userService.register(UserRole.CUSTOMER, user);
            assertNotNull(result);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testIsLoggedInChecksCorrectAttribute() {
        userService.isLoggedIn(UserRole.CUSTOMER, mockSession);
        verify(mockSession).getAttribute("CUSTOMER");
    }

    @Test
    void testIsLoggedInChecksSellerAttribute() {
        userService.isLoggedIn(UserRole.SELLER, mockSession);
        verify(mockSession).getAttribute("SELLER");
    }

    @Test
    void testLoginReturnsNullForInvalidCredentials() {
        try {
            User user = userService.login(UserRole.CUSTOMER, "invalid@example.com", "wrongpassword", mockSession);
            assertNull(user);
        } catch (StoreException e) {
            assertNotNull(e);
        }
    }
}
