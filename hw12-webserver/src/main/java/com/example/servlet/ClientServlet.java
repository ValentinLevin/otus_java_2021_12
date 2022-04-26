package com.example.servlet;

import com.example.dto.ClientDTO;
import com.example.services.ClientService;
import com.example.services.TemplateProcessor;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ClientServlet extends HttpServlet {
    private static final String CLIENTS_PAGE_TEMPLATE = "clients.ftl";
    private static final String CLIENT_NAME_PARAM_NAME = "name";
    private static final String CLIENT_ADDRESS_STREET_PARAM_NAME = "address";
    private static final String CLIENT_PHONE_NUMBER_PARAM_NAME = "phone";
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String clientName = req.getParameter(CLIENT_NAME_PARAM_NAME);
        String clientAddress = req.getParameter(CLIENT_ADDRESS_STREET_PARAM_NAME);
        String[] clientPhoneNumber = req.getParameterValues(CLIENT_PHONE_NUMBER_PARAM_NAME);
        clientService.addClient(clientName, clientAddress, clientPhoneNumber);
        resp.sendRedirect("/clients");
    }
}
