package com.vladislavlevchik.filter;

import com.vladislavlevchik.exception.authentication.IncorrectPasswordException;
import com.vladislavlevchik.exception.authentication.UserNotFoundByLoginException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/sign-in")
public class AuthenticationFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        try {
            super.doFilter(req, res, chain);
        } catch (IncorrectPasswordException | UserNotFoundByLoginException e) {
            res.sendRedirect("/sign-in?error=true");
        }
    }
}
