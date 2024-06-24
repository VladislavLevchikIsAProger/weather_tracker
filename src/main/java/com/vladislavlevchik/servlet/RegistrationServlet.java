package com.vladislavlevchik.servlet;

import com.vladislavlevchik.dto.UserRequestDto;
import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.repository.SessionRepository;
import com.vladislavlevchik.repository.UserRepository;
import com.vladislavlevchik.utils.MapperUtil;
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
import java.util.UUID;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    private final UserRepository userRepository = new UserRepository();

    private final SessionRepository sessionRepository = new SessionRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext context = new WebContext(webExchange);

        templateEngine.process("signup", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .login(req.getParameter("login"))
                .password(req.getParameter("password"))
                .build();

        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");

        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext context = new WebContext(webExchange);

        if (userRepository.findByLogin(userRequestDto.getLogin()).isPresent()) {
            context.setVariable("error", "login is already in use");
            templateEngine.process("signup", context, resp.getWriter());
            return;
        }

        User user = MapperUtil.convertToEntity(userRequestDto);

        userRepository.save(user);

        Session session = Session.builder()
//                .id(UUID.randomUUID())
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        sessionRepository.save(session);

        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        resp.addCookie(cookie);
//
//
//        context.setVariable("username", user.getLogin());
//        context.setVariable("password", user.getPassword());

        templateEngine.process("home", context, resp.getWriter());
    }
}
