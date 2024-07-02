package com.vladislavlevchik.servlet;

import com.vladislavlevchik.dto.WeatherApiResponseDirectDto;
import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.repository.LocationRepository;
import com.vladislavlevchik.repository.UserRepository;
import com.vladislavlevchik.service.AuthenticationService;
import com.vladislavlevchik.service.LocationService;
import com.vladislavlevchik.service.WeatherApiService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.List;

@WebServlet("/locations")
public class LocationsServlet extends HttpServlet {
    private final WeatherApiService weatherApiService = new WeatherApiService();
    private final AuthenticationService authenticationService = new AuthenticationService();

    private final LocationService locationService = new LocationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext context = new WebContext(webExchange);

        String sessionId = authenticationService.findSessionIdCookie(req.getCookies()).getValue();

        Session session = authenticationService.getSessionIfValid(sessionId);

        String cityName = req.getParameter("city_name");

        List<WeatherApiResponseDirectDto> list = weatherApiService.getList(cityName);

        context.setVariable("weathers", list);
        context.setVariable("login", session.getUser().getLogin());
        context.setVariable("city_name", cityName);

        templateEngine.process("locations", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Double lat = Double.valueOf(req.getParameter("lat"));
        Double lon = Double.valueOf(req.getParameter("lon"));
        String name = req.getParameter("name");

        String sessionId = authenticationService.findSessionIdCookie(req.getCookies()).getValue();

        Session session = authenticationService.getSessionIfValid(sessionId);

        locationService.addLocation(session.getUser(), name, lat, lon);

        resp.sendRedirect("home");
    }
}
