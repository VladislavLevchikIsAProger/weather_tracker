package com.vladislavlevchik.servlet;

import com.vladislavlevchik.dto.CoordinatesRequestDto;
import com.vladislavlevchik.dto.WeatherResponseDto;
import com.vladislavlevchik.entity.Session;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static java.lang.Double.parseDouble;

@WebServlet("/home")
public class HomeServlet extends WeatherTrackerBaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionId = authenticationService.findSessionIdCookie(req.getCookies()).getValue();

        Session session = authenticationService.getSessionIfValid(sessionId);

        List<WeatherResponseDto> weathers = locationService.getWeathers(session.getUser().getLocations());

        context.setVariable("login", session.getUser().getLogin());
        context.setVariable("weathers", weathers);

        templateEngine.process("home", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CoordinatesRequestDto coordinates = CoordinatesRequestDto.builder()
                .lat(parseDouble(req.getParameter("lat")))
                .lon(parseDouble(req.getParameter("lon")))
                .build();

        String sessionId = authenticationService.findSessionIdCookie(req.getCookies()).getValue();

        Session session = authenticationService.getSessionIfValid(sessionId);

        locationService.deleteLocationFromUser(coordinates, session.getUser());

        resp.sendRedirect("/home");
    }
}
