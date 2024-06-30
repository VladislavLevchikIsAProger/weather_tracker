package com.vladislavlevchik.servlet;

import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.service.AuthenticationService;
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
    private final AuthenticationService authenticationService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);

        WebContext context = new WebContext(webExchange);

        String sessionId = authenticationService.findSessionIdCookie(req.getCookies()).getValue();

        Session session = authenticationService.getSessionIfValid(sessionId);

//        context.setVariable("isLoggedIn", true);
        context.setVariable("login", session.getUser().getLogin());

        templateEngine.process("home", context, resp.getWriter());
    }
}
