package com.vladislavlevchik.servlet.authentication;

import com.vladislavlevchik.dto.UserRequestDto;
import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.service.AuthenticationService;
import com.vladislavlevchik.servlet.WeatherTrackerBaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet("/sign-in")
public class SignInServlet extends WeatherTrackerBaseServlet {

    private final AuthenticationService authenticationService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext context = new WebContext(webExchange);

        templateEngine.process("signin", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .login(req.getParameter("login"))
                .password(req.getParameter("password"))
                .build();

        User user = authenticationService.getUserByLoginIfValid(userRequestDto);

        Session session = authenticationService.saveSession(user);

        addCookie(resp, session);

        resp.sendRedirect("/home");
    }
}
