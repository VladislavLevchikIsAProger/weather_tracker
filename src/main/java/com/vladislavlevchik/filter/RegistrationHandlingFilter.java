package com.vladislavlevchik.filter;

import com.vladislavlevchik.exception.UserAlreadyExistException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebFilter("/sign-up")
public class RegistrationHandlingFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");

        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, res);

        WebContext context = new WebContext(webExchange);

        try {
            super.doFilter(req, res, chain);
        } catch (UserAlreadyExistException e) {
            context.setVariable("error", "login is already in use");
            templateEngine.process("signup", context, res.getWriter());
        }
    }
}
