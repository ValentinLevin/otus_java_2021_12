package com.example.servlet;

import com.example.dto.ClientDTO;
import com.example.services.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class ClientApiServlet extends HttpServlet {
    private static final String CLIENT_NAME_PARAM_NAME = "name";
    private static final String CLIENT_ADDRESS_STREET_NAME = "address";
    private static final String CLIENT_PHONE_NUMBER = "phone";
    private static final int ID_PATH_PARAM_POSITION = 1;

    private final ClientService clientService;
    private final ObjectMapper objectMapper;

    public ClientApiServlet(ClientService clientService, ObjectMapper objectMapper) {
        this.clientService = clientService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long requiredClientId = extractIdFromRequest(req);
        String value;
        if (requiredClientId == null) {
            List<ClientDTO> clientDTOs = clientService.findAll();
            value = objectMapper.writeValueAsString(clientDTOs);
        } else {
            ClientDTO clientDTO = clientService.findById(requiredClientId);
            value = clientDTO == null ? "{}" : objectMapper.writeValueAsString(clientDTO);
        }
        resp.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outStream = resp.getOutputStream();
        outStream.print(value);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String clientName = req.getParameter(CLIENT_NAME_PARAM_NAME);
        String clientAddress = req.getParameter(CLIENT_ADDRESS_STREET_NAME);
        String[] clientPhoneNumber = req.getParameterValues(CLIENT_PHONE_NUMBER);
        clientService.addClient(clientName, clientAddress, clientPhoneNumber);
        resp.sendRedirect("/clients");
    }

    private Long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1)? path[ID_PATH_PARAM_POSITION]: null;
        return id != null ? Long.parseLong(id) : null;
    }
}
