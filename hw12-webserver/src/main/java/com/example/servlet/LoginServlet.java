package com.example.servlet;

import com.example.services.TemplateProcessor;
import com.example.services.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Optional;

public class LoginServlet extends HttpServlet {
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_RETURN_URL = "returnUri";
    private static final int MAX_INACTIVE_INTERVAL = 30;
    private static final String LOGIN_PAGE_TEMPLATE = "login.ftl";
    private static final String DEFAULT_START_PAGE = "clients";

    private final TemplateProcessor templateProcessor;
    private final UserService userService;

    public LoginServlet(TemplateProcessor templateProcessor, UserService userService) {
        this.templateProcessor = templateProcessor;
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter(PARAM_USERNAME);
        String password = req.getParameter(PARAM_PASSWORD);
        if (this.userService.authenticate(username, password)) {
            HttpSession session = req.getSession();
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
            String returnUrl = Optional.ofNullable(req.getParameter(PARAM_RETURN_URL)).orElse(DEFAULT_START_PAGE);
            resp.sendRedirect(returnUrl);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String returnUrl = Optional.ofNullable(req.getParameter(PARAM_RETURN_URL)).orElse("");
        String page = templateProcessor.getPage(LOGIN_PAGE_TEMPLATE, Collections.singletonMap(PARAM_RETURN_URL, returnUrl));
        resp.setContentType("text/html");
        try (Writer stream = resp.getWriter()) {
            stream.write(page);
        }
    }
}
