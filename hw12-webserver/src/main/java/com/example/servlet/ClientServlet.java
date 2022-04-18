package com.example.servlet;

import com.example.dto.ClientDTO;
import com.example.services.ClientService;
import com.example.services.TemplateProcessor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ClientServlet extends HttpServlet {
    private static final String CLIENTS_PAGE_TEMPLATE = "clients.ftl";
    private final ClientService clientService;
    private final TemplateProcessor templateProcessor;

    public ClientServlet(ClientService clientService, TemplateProcessor templateProcessor) {
        this.clientService = clientService;
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ClientDTO> clients = clientService.findAll();
        String page = templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, Collections.singletonMap("clients", clients));
        resp.setContentType("text/html");
        resp.getWriter().println(page);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
