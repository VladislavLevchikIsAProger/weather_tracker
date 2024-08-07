package com.vladislavlevchik.servlet.authentication;

import com.vladislavlevchik.dto.UserRequestDto;
import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.servlet.WeatherTrackerBaseServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.vladislavlevchik.utils.ValidationUtil.validate;

@WebServlet("/sign-up")
public class SignUpServlet extends WeatherTrackerBaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        templateEngine.process("signup", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .login(req.getParameter("login"))
                .password(req.getParameter("password"))
                .build();

        validate(userRequestDto);

        User user = authenticationService.saveUser(userRequestDto);

        Session session = authenticationService.saveSession(user);

        addCookie(resp, session);

        resp.sendRedirect("/home");
    }
}
