package com.vladislavlevchik.servlet;

import com.vladislavlevchik.dto.UserRequestDto;
import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.repository.SessionRepository;
import com.vladislavlevchik.repository.UserRepository;
import com.vladislavlevchik.service.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/sign-up")
public class RegistrationServlet extends HttpServlet {
    private final AuthenticationService authenticationService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext context = new WebContext(webExchange);

        templateEngine.process("signup", context, resp.getWriter());
    }

    //TODO добавить хэширование пароля
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .login(req.getParameter("login"))
                .password(req.getParameter("password"))
                .build();

        User user = authenticationService.saveUser(userRequestDto);

        Session session = authenticationService.saveSession(user);

        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        cookie.setMaxAge(60 * 60 * 24);
        resp.addCookie(cookie);

        resp.sendRedirect("/home");
    }
}
