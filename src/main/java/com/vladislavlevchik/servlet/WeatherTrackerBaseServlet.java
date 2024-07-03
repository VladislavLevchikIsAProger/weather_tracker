package com.vladislavlevchik.servlet;

import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.service.AuthenticationService;
import com.vladislavlevchik.service.LocationService;
import com.vladislavlevchik.service.WeatherApiService;
import com.vladislavlevchik.utils.WebContextUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;

public abstract class WeatherTrackerBaseServlet extends HttpServlet {
    protected final AuthenticationService authenticationService = new AuthenticationService();
    protected final WeatherApiService weatherApiService = new WeatherApiService();
    protected final LocationService locationService = new LocationService();

    protected TemplateEngine templateEngine;

    protected WebContext context;

    @Override
    public void init() {
        templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        context = WebContextUtil.buildWebContext(req, resp, getServletContext());

        super.service(req, resp);
    }

    public void addCookie(HttpServletResponse resp, Session session) {
        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        cookie.setMaxAge(60 * 60 * 24);
        resp.addCookie(cookie);
    }

    public void deleteCookie(HttpServletResponse resp, Cookie cookie) {
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }

}
