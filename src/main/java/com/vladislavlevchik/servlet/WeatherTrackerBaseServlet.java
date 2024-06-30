package com.vladislavlevchik.servlet;

import com.vladislavlevchik.entity.Session;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

public class WeatherTrackerBaseServlet extends HttpServlet {

    public void addCookie(HttpServletResponse resp, Session session){
        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        cookie.setMaxAge(60 * 60 * 24);
        resp.addCookie(cookie);
    }

    public void deleteCookie(HttpServletResponse resp, Cookie cookie){
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }

}
