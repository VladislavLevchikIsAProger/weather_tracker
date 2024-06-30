package com.vladislavlevchik.filter;

import com.vladislavlevchik.exception.UserAlreadyExistException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/sign-up")
public class RegistrationHandlingFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        try {
            super.doFilter(req, res, chain);
        } catch (UserAlreadyExistException e) {
            res.sendRedirect("/sign-up?error=true");
        }
    }
}
