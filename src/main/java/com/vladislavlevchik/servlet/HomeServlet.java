package com.vladislavlevchik.servlet;

import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.service.AuthorizationService;
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

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final AuthorizationService authorizationService = new AuthorizationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext context = new WebContext(webExchange);

        String sessionId = authorizationService.findSessionIdInCookies(req.getCookies());

        Session session = authorizationService.getSessionIfValid(sessionId);

        context.setVariable("isLoggedIn", true);
        context.setVariable("login", session.getUser().getLogin());

        templateEngine.process("home", context, resp.getWriter());
    }
}
