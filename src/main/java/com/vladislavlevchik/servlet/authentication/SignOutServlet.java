package com.vladislavlevchik.servlet.authentication;

import com.vladislavlevchik.servlet.WeatherTrackerBaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/logout")
public class SignOutServlet extends WeatherTrackerBaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cookie cookie = authenticationService.findSessionIdCookie(req.getCookies());

        deleteCookie(resp, cookie);

        authenticationService.deleteSession(cookie.getValue());

        resp.sendRedirect("/sign-in");
    }
}
