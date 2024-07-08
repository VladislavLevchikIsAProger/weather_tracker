package com.vladislavlevchik.service;

import com.vladislavlevchik.dto.UserRequestDto;
import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.exception.authentication.*;
import com.vladislavlevchik.repository.SessionRepository;
import com.vladislavlevchik.util.HibernateTestUtil;
import jakarta.servlet.http.Cookie;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mindrot.jbcrypt.BCrypt.checkpw;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationServiceTest {

    private AuthenticationService authenticationService;
    private SessionFactory sessionFactory;

    @BeforeAll
    void setUp() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
        authenticationService = new AuthenticationService(sessionFactory);
    }

    @AfterEach
    void setUpAndCleanUp() {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.createQuery("DELETE FROM Session").executeUpdate();
            session.createQuery("DELETE FROM User").executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Nested
    @DisplayName("test user login functionality")
    class UserTest {
        @Test
        void shouldSuccessfullySaveNewUser() {
            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login("vladlevchik")
                    .password("password")
                    .build();

            User user = authenticationService.saveUser(userRequestDto);

            assertNotNull(user.getId());
        }

        @Test
        void shouldThrowExceptionWhenUserAlreadyRegistered() {
            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login("vladlevchik")
                    .password("password")
                    .build();

            authenticationService.saveUser(userRequestDto);

            UserRequestDto duplicateUserRequestDto = UserRequestDto.builder()
                    .login("vladlevchik")
                    .password("password")
                    .build();

            assertThrows(UserAlreadyExistException.class,
                    () -> authenticationService.saveUser(duplicateUserRequestDto));
        }

        @Test
        void shouldHashPasswordWhenSavingUser() {
            String password = "password";

            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login("vladlevchik")
                    .password(password)
                    .build();

            User user = authenticationService.saveUser(userRequestDto);

            assertNotEquals(password, user.getPassword());
            assertTrue(checkpw(password, user.getPassword()));
        }

        @Test
        void shouldAuthenticateUserWithValidCredentials() {
            String login = "vladlevchik";
            String password = "password";

            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login(login)
                    .password(password)
                    .build();

            authenticationService.saveUser(userRequestDto);

            UserRequestDto findUserDto = UserRequestDto.builder()
                    .login(login)
                    .password(password)
                    .build();

            User user = authenticationService.getUserByLoginIfValid(findUserDto);

            assertEquals(login, user.getLogin());
            assertTrue(checkpw(password, user.getPassword()));
            assertNotNull(user.getId());

        }

        @Test
        void shouldThrowExceptionWhenLoginNotFound() {
            String password = "password";

            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login("vladlevchik")
                    .password(password)
                    .build();

            authenticationService.saveUser(userRequestDto);

            UserRequestDto findUserDto = UserRequestDto.builder()
                    .login("login")
                    .password(password)
                    .build();

            assertThrows(UserNotFoundByLoginException.class,
                    () -> authenticationService.getUserByLoginIfValid(findUserDto));
        }

        @Test
        void shouldThrowExceptionWhenPasswordIsIncorrect() {
            String login = "vladlevchik";
            String password = "password";

            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login(login)
                    .password(password)
                    .build();

            authenticationService.saveUser(userRequestDto);

            UserRequestDto findUserDto = UserRequestDto.builder()
                    .login(login)
                    .password("dummy")
                    .build();

            assertThrows(IncorrectPasswordException.class,
                    () -> authenticationService.getUserByLoginIfValid(findUserDto));
        }
    }

    @Nested
    @DisplayName("test session functionality")
    class SessionTest {
        @Test
        void shouldSaveSessionForUser() {
            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login("vladlevchik")
                    .password("password")
                    .build();

            User user = authenticationService.saveUser(userRequestDto);

            Session session = authenticationService.saveSession(user);

            assertNotNull(session.getId());
        }

        @Test
        void shouldThrowCookieNotFoundExceptionWhenCookiesArrayIsEmpty() {
            assertThrows(CookieNotFoundException.class, () -> {
                authenticationService.findSessionIdCookie(new Cookie[]{});
            });
        }

        @Test
        void shouldThrowCookieNotFoundExceptionWhenCookiesArrayIsNull() {
            assertThrows(CookieNotFoundException.class, () -> {
                authenticationService.findSessionIdCookie(null);
            });
        }

        @Test
        void shouldThrowSessionIdNotFoundExceptionWhenSessionIdCookieIsAbsent() {
            Cookie[] cookies = {
                    new Cookie("someCookie", "value"),
                    new Cookie("anotherCookie", "value")
            };

            assertThrows(SessionIdNotFoundException.class, () -> {
                authenticationService.findSessionIdCookie(cookies);
            });
        }

        @Test
        void shouldReturnSessionIdCookieWhenItIsPresent() {
            Cookie sessionIdCookie = new Cookie("sessionId", "someValue");
            Cookie[] cookies = {
                    new Cookie("someCookie", "value"),
                    sessionIdCookie,
                    new Cookie("anotherCookie", "value")
            };

            Cookie result = authenticationService.findSessionIdCookie(cookies);

            assertNotNull(result);
            assertEquals("sessionId", result.getName());
            assertEquals("someValue", result.getValue());
        }

        @Test
        void getSessionIfValid_ValidSession_ReturnsSession() {
            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login("vladlevchik")
                    .password("password")
                    .build();

            User user = authenticationService.saveUser(userRequestDto);

            Session sessionFromDB = authenticationService.saveSession(user);

            Session session = authenticationService.getSessionIfValid(sessionFromDB.getId().toString());

            assertNotNull(session);
            assertEquals(sessionFromDB.getId(), session.getId());
        }

        @Test
        void getSessionIfValid_SessionNotFound_ThrowsSessionIdNotFoundException() {
            String nonExistingSessionId = UUID.randomUUID().toString();

            assertThrows(SessionIdNotFoundException.class, () -> authenticationService.getSessionIfValid(nonExistingSessionId));
        }

        @Test
        void getSessionIfValid_SessionExpired_ThrowsSessionExpiredException() {
            SessionRepository sessionRepository = new SessionRepository();

            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login("vladlevchik")
                    .password("password")
                    .build();

            User user = authenticationService.saveUser(userRequestDto);

            Session session = Session.builder()
                    .user(user)
                    .expiresAt(LocalDateTime.now().minusDays(1))
                    .build();

            Session sessionFromDB = sessionRepository.save(session);

            assertThrows(SessionExpiredException.class, () -> authenticationService.getSessionIfValid(sessionFromDB.getId().toString()));
        }

        @Test
        void shouldDeleteSessionById() {
            UserRequestDto userRequestDto = UserRequestDto.builder()
                    .login("vladlevchik")
                    .password("password")
                    .build();

            User user = authenticationService.saveUser(userRequestDto);

            Session session = authenticationService.saveSession(user);

            authenticationService.deleteSession(session.getId().toString());

            assertThrows(SessionIdNotFoundException.class,
                    () -> authenticationService.getSessionIfValid(session.getId().toString()));
        }
    }
}
