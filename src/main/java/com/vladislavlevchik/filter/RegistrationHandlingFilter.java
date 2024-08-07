package com.vladislavlevchik.filter;

import com.vladislavlevchik.exception.authentication.InvalidLoginException;
import com.vladislavlevchik.exception.authentication.InvalidPasswordException;
import com.vladislavlevchik.exception.authentication.UserAlreadyExistException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;

import static com.vladislavlevchik.utils.WebContextUtil.buildWebContext;

@WebFilter("/sign-up")
public class RegistrationHandlingFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");

        WebContext context = buildWebContext(req, res, getServletContext());

        try {
            super.doFilter(req, res, chain);
        } catch (InvalidLoginException e) {
            context.setVariable("errorLogin", "errorLogin");
            templateEngine.process("signup", context, res.getWriter());
        } catch (InvalidPasswordException e) {
            context.setVariable("errorPassword", "errorPassword");
            templateEngine.process("signup", context, res.getWriter());
        } catch (UserAlreadyExistException e) {
            res.sendRedirect("/sign-up?error=true");
        }
    }
}
