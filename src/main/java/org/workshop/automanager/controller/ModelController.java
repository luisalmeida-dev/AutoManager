package org.workshop.automanager.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.workshop.automanager.dto.request.ModelRequestDTO;
import org.workshop.automanager.dto.response.ModelResponseDTO;
import org.workshop.automanager.service.ModelService;

import java.util.List;

@RestController
@RequestMapping("/models")
public class ModelController {
    @Autowired
    private ModelService modelService;

    @PostMapping
    public ResponseEntity<HttpStatus> createModel(@Valid @RequestBody ModelRequestDTO request) {
        modelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelResponseDTO> getModelById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(modelService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ModelResponseDTO>> getAllModels() {
        return ResponseEntity.ok().body(modelService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@Valid @RequestBody ModelRequestDTO request, @PathVariable Integer id) {
        modelService.update(request, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Integer id) {
        modelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
