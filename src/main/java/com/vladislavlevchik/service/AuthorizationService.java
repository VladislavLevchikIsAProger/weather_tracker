package com.vladislavlevchik.service;

import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.exception.SessionIdNotFoundException;
import com.vladislavlevchik.exception.EmptyCookieException;
import com.vladislavlevchik.exception.SessionExpiredException;
import com.vladislavlevchik.repository.SessionRepository;
import jakarta.servlet.http.Cookie;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class AuthorizationService {

    private final SessionRepository sessionRepository = new SessionRepository();

    public String findSessionIdInCookies(Cookie[] cookies) {
        if (cookies == null || cookies.length < 1) {
            throw new EmptyCookieException();
        }

        String sessionId = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("sessionId")) {
                sessionId = cookie.getValue();
                break;
            }
        }

        if (sessionId == null) {
            throw new SessionIdNotFoundException();
        }

        return sessionId;
    }

    public Session getSessionIfValid(String sessionId) {
        Optional<Session> optionalSession = sessionRepository.findById(UUID.fromString(sessionId));
        if (optionalSession.isEmpty()) {
            throw new SessionIdNotFoundException();
        }

        Session session = optionalSession.get();
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new SessionExpiredException();
        }

        return session;
    }
}