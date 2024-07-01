package com.vladislavlevchik.servlet;

import com.vladislavlevchik.dto.WeatherApiResponseDto;
import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.service.AuthenticationService;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext context = new WebContext(webExchange);

        String sessionId = authenticationService.findSessionIdCookie(req.getCookies()).getValue();

        Session session = authenticationService.getSessionIfValid(sessionId);

        String cityName = req.getParameter("city_name");

        List<WeatherApiResponseDto> list = weatherApiService.getList(cityName);

        context.setVariable("weathers", list);
        context.setVariable("login", session.getUser().getLogin());
        context.setVariable("city_name", cityName);

        templateEngine.process("locations", context, resp.getWriter());
    }
}
