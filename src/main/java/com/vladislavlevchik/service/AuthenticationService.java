package com.vladislavlevchik.service;

import com.vladislavlevchik.dto.UserRequestDto;
import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.exception.authentication.*;
import com.vladislavlevchik.repository.SessionRepository;
import com.vladislavlevchik.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static com.vladislavlevchik.utils.MapperUtil.convertToEntity;
import static org.mindrot.jbcrypt.BCrypt.checkpw;
import static org.mindrot.jbcrypt.BCrypt.hashpw;

public class AuthenticationService {

    private final SessionRepository sessionRepository = new SessionRepository();

    private final UserRepository userRepository = new UserRepository();

    public User saveUser(UserRequestDto user) {
        String hashedPassword = hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        return userRepository.save(convertToEntity(user));
    }

    public Session saveSession(User user) {
        Session session = Session.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        return sessionRepository.save(session);
    }

    public Cookie findSessionIdCookie(Cookie[] cookies) {
        if (cookies == null || cookies.length < 1) {
            throw new CookieNotFoundException();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> "sessionId".equals(cookie.getName()))
                .findFirst()
                .orElseThrow(SessionIdNotFoundException::new);
    }

    public Session getSessionIfValid(String sessionId) {
        Session session = sessionRepository.findById(UUID.fromString(sessionId))
                .orElseThrow(SessionIdNotFoundException::new);

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new SessionExpiredException();
        }

        return session;
    }

    public void deleteSession(String uuid) {
        sessionRepository.deleteById(UUID.fromString(uuid));
    }

    public User getUserByLoginIfValid(UserRequestDto userRequestDto) {
        User user = userRepository.findByLogin(userRequestDto.getLogin())
                .orElseThrow(UserNotFoundByLoginException::new);

        if (!checkpw(userRequestDto.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        return user;
    }
}