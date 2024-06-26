package com.vladislavlevchik.service;

import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.exception.SessionIdNotFoundException;
import com.vladislavlevchik.exception.EmptyCookieException;
import com.vladislavlevchik.exception.SessionExpiredException;
import com.vladislavlevchik.repository.SessionRepository;
import jakarta.servlet.http.Cookie;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class AuthorizationService {

    private final SessionRepository sessionRepository = new SessionRepository();

    public String findSessionIdInCookies(Cookie[] cookies) {
        if (cookies == null || cookies.length < 1) {
            throw new EmptyCookieException();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> "sessionId".equals(cookie.getName()))
                .map(Cookie::getValue)
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
}