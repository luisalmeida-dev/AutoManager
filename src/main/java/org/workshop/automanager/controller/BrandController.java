package org.workshop.automanager.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.workshop.automanager.dto.request.BrandRequestDTO;
import org.workshop.automanager.dto.response.BrandresponseDTO;
import org.workshop.automanager.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/brands")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @PostMapping
    public ResponseEntity<HttpStatus> createBrand(@Valid @RequestBody BrandRequestDTO request) {
        brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandresponseDTO> getBrandById(@PathVariable int id) {
        return ResponseEntity.ok(brandService.getBrand(id));
    }

    @GetMapping
    public ResponseEntity<List<BrandresponseDTO>> getAll() {
        return ResponseEntity.ok(brandService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@Valid @RequestBody BrandRequestDTO request, @PathVariable int id) {
        brandService.update(request, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
