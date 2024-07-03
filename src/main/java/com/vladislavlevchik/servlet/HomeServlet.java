package com.vladislavlevchik.servlet;

import com.vladislavlevchik.dto.WeatherResponseDto;
import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.service.AuthenticationService;
import com.vladislavlevchik.service.LocationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends WeatherTrackerBaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String sessionId = authenticationService.findSessionIdCookie(req.getCookies()).getValue();

        Session session = authenticationService.getSessionIfValid(sessionId);

        List<WeatherResponseDto> weathers = locationService.getWeathers(session.getUser().getLocations());

        context.setVariable("login", session.getUser().getLogin());

        templateEngine.process("home", context, resp.getWriter());
    }
}
