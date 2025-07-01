package org.workshop.automanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.workshop.automanager.dto.request.UpdatePasswordRequestDTO;
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
    public ResponseEntity<HttpStatus> createUser(@RequestBody UserRequestDTO request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        return ResponseEntity.ok().body(userService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer id , @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok().body(userService.updateUserById(id, request));
    }

    @PutMapping("/update-password")
    public ResponseEntity<HttpStatus> updatePassword(@RequestBody UpdatePasswordRequestDTO request) {
        userService.updatePassword(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Integer id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
