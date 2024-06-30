package com.vladislavlevchik.servlet;

import com.vladislavlevchik.service.AuthenticationService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/logout")
public class SignOutServlet extends HttpServlet {
    private final AuthenticationService authenticationService = new AuthenticationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cookie cookie = authenticationService.findSessionIdCookie(req.getCookies());

        cookie.setMaxAge(0);
        resp.addCookie(cookie);

        authenticationService.deleteSession(cookie.getValue());

        resp.sendRedirect("/sign-in");
    }
}
