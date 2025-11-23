package org.workshop.automanager.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.workshop.automanager.dto.request.CarRequestDTO;
import org.workshop.automanager.dto.response.CarResponseDTO;
import org.workshop.automanager.service.CarService;

import java.util.List;

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
