package com.example.controller;

import com.example.dto.ClientDTO;
import com.example.service.ClientService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClientRestController {
    private final ClientService clientService;

    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping(
            value = "/api/client/save",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> clientSave(@RequestBody ClientDTO client) {
        ClientDTO processedClientDTO = clientService.saveClient(client);
        return ResponseEntity.ok(processedClientDTO);
    }
}
