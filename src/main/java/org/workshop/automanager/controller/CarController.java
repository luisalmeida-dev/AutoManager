package org.workshop.automanager.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workshop.automanager.dto.request.CarRequestDTO;
import org.workshop.automanager.service.CarService;

@RestController
@RequestMapping("/cars")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping
    public ResponseEntity<HttpStatus> create (@Valid @RequestBody CarRequestDTO request){
        carService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
