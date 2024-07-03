package com.vladislavlevchik.filter;

import com.vladislavlevchik.exception.AuthorizationException;
import com.vladislavlevchik.exception.DuplicateLocationException;
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

@WebFilter(urlPatterns = {"/locations", "/home"})
public class HomeAndLocationsHandlingFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        TemplateEngine templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");

        WebContext context = buildWebContext(req, res, getServletContext());

        try {
            super.doFilter(req, res, chain);
        } catch (AuthorizationException e) {
            templateEngine.process("home", context, res.getWriter());
        } catch (DuplicateLocationException e){
            res.sendRedirect("home");
        }
    }

}
