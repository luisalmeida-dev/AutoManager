package org.workshop.automanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workshop.automanager.dto.request.UserRequestDTO;
import org.workshop.automanager.service.UserService;

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
