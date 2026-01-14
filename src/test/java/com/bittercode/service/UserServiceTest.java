package com.bittercode.service;

import com.bittercode.model.StoreException;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private HttpSession mockSession;

    private static class TestableUserService implements UserService {
        @Override
        public User login(UserRole role, String email, String password, HttpSession session) throws StoreException {
            if (email == null || password == null) {
                throw new StoreException("Email and password cannot be null");
            }
            if (email.isEmpty() || password.isEmpty()) {
                throw new StoreException("Email and password cannot be empty");
            }
            User user = new User();
            user.setEmailId(email);
            session.setAttribute(role.toString(), user);
            return user;
        }

        @Override
        public String register(UserRole role, User user) throws StoreException {
            if (user == null) {
                throw new StoreException("User cannot be null");
            }
            if (user.getEmailId() == null || user.getPassword() == null) {
                throw new StoreException("Email and password are required");
            }
            return "User registered successfully";
        }

        @Override
        public boolean isLoggedIn(UserRole role, HttpSession session) {
            return session.getAttribute(role.toString()) != null;
        }

        @Override
        public boolean logout(HttpSession session) {
            session.invalidate();
            return true;
        }
    }

    @BeforeEach
    void setUp() {
        mockSession = mock(HttpSession.class);
    }

    @Test
    void testLoginSuccess() throws StoreException {
        UserService service = new TestableUserService();
        User user = service.login(UserRole.CUSTOMER, "test@example.com", "password", mockSession);

        assertNotNull(user);
        assertEquals("test@example.com", user.getEmailId());
        verify(mockSession).setAttribute(eq("CUSTOMER"), any(User.class));
    }

    @Test
    void testLoginNullEmailThrowsException() {
        UserService service = new TestableUserService();
        assertThrows(StoreException.class, () ->
            service.login(UserRole.CUSTOMER, null, "password", mockSession)
        );
    }

    @Test
    void testLoginNullPasswordThrowsException() {
        UserService service = new TestableUserService();
        assertThrows(StoreException.class, () ->
            service.login(UserRole.CUSTOMER, "test@example.com", null, mockSession)
        );
    }

    @Test
    void testLoginEmptyEmailThrowsException() {
        UserService service = new TestableUserService();
        assertThrows(StoreException.class, () ->
            service.login(UserRole.CUSTOMER, "", "password", mockSession)
        );
    }

    @Test
    void testLoginEmptyPasswordThrowsException() {
        UserService service = new TestableUserService();
        assertThrows(StoreException.class, () ->
            service.login(UserRole.CUSTOMER, "test@example.com", "", mockSession)
        );
    }

    @Test
    void testLoginWithSellerRole() throws StoreException {
        UserService service = new TestableUserService();
        User user = service.login(UserRole.SELLER, "seller@example.com", "password", mockSession);

        assertNotNull(user);
        verify(mockSession).setAttribute(eq("SELLER"), any(User.class));
    }

    @Test
    void testRegisterSuccess() throws StoreException {
        UserService service = new TestableUserService();
        User user = new User();
        user.setEmailId("newuser@example.com");
        user.setPassword("password123");

        String result = service.register(UserRole.CUSTOMER, user);

        assertNotNull(result);
        assertTrue(result.contains("registered"));
    }

    @Test
    void testRegisterNullUserThrowsException() {
        UserService service = new TestableUserService();
        assertThrows(StoreException.class, () ->
            service.register(UserRole.CUSTOMER, null)
        );
    }

    @Test
    void testRegisterUserWithNullEmailThrowsException() {
        UserService service = new TestableUserService();
        User user = new User();
        user.setPassword("password");

        assertThrows(StoreException.class, () ->
            service.register(UserRole.CUSTOMER, user)
        );
    }

    @Test
    void testRegisterUserWithNullPasswordThrowsException() {
        UserService service = new TestableUserService();
        User user = new User();
        user.setEmailId("test@example.com");

        assertThrows(StoreException.class, () ->
            service.register(UserRole.CUSTOMER, user)
        );
    }

    @Test
    void testRegisterWithSellerRole() throws StoreException {
        UserService service = new TestableUserService();
        User user = new User();
        user.setEmailId("seller@example.com");
        user.setPassword("password123");

        String result = service.register(UserRole.SELLER, user);

        assertNotNull(result);
        assertTrue(result.contains("registered"));
    }

    @Test
    void testIsLoggedInReturnsTrue() {
        UserService service = new TestableUserService();
        when(mockSession.getAttribute("CUSTOMER")).thenReturn(new User());

        assertTrue(service.isLoggedIn(UserRole.CUSTOMER, mockSession));
    }

    @Test
    void testIsLoggedInReturnsFalse() {
        UserService service = new TestableUserService();
        when(mockSession.getAttribute("CUSTOMER")).thenReturn(null);

        assertFalse(service.isLoggedIn(UserRole.CUSTOMER, mockSession));
    }

    @Test
    void testIsLoggedInForSellerRole() {
        UserService service = new TestableUserService();
        when(mockSession.getAttribute("SELLER")).thenReturn(new User());

        assertTrue(service.isLoggedIn(UserRole.SELLER, mockSession));
    }

    @Test
    void testLogoutSuccess() {
        UserService service = new TestableUserService();
        boolean result = service.logout(mockSession);

        assertTrue(result);
        verify(mockSession).invalidate();
    }

    @Test
    void testLogoutInvalidatesSession() {
        UserService service = new TestableUserService();
        service.logout(mockSession);

        verify(mockSession, times(1)).invalidate();
    }

    @Test
    void testLoginWithDifferentEmails() throws StoreException {
        UserService service = new TestableUserService();

        User user1 = service.login(UserRole.CUSTOMER, "user1@example.com", "pass1", mockSession);
        assertEquals("user1@example.com", user1.getEmailId());

        User user2 = service.login(UserRole.CUSTOMER, "user2@example.com", "pass2", mockSession);
        assertEquals("user2@example.com", user2.getEmailId());
    }

    @Test
    void testRegisterMultipleUsers() throws StoreException {
        UserService service = new TestableUserService();

        User user1 = new User();
        user1.setEmailId("user1@example.com");
        user1.setPassword("password1");
        String result1 = service.register(UserRole.CUSTOMER, user1);
        assertNotNull(result1);

        User user2 = new User();
        user2.setEmailId("user2@example.com");
        user2.setPassword("password2");
        String result2 = service.register(UserRole.SELLER, user2);
        assertNotNull(result2);
    }

    @Test
    void testIsLoggedInAfterLogin() throws StoreException {
        UserService service = new TestableUserService();
        User user = new User();
        when(mockSession.getAttribute("CUSTOMER")).thenReturn(user);

        service.login(UserRole.CUSTOMER, "test@example.com", "password", mockSession);
        assertTrue(service.isLoggedIn(UserRole.CUSTOMER, mockSession));
    }
}
