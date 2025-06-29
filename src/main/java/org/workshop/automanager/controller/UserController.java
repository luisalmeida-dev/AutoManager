package org.workshop.automanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.workshop.automanager.dto.request.UserRequestDTO;
import org.workshop.automanager.dto.response.UserResponseDTO;
import org.workshop.automanager.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public void createUser(@RequestBody UserRequestDTO request) {
        userService.createUser(request);
    }
}
