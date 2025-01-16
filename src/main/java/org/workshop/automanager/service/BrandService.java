package org.workshop.automanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.workshop.automanager.dto.request.BrandRequestDTO;
import org.workshop.automanager.dto.response.BrandresponseDTO;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.exception.NotFoundException;
import org.workshop.automanager.mapper.BrandMapper;
import org.workshop.automanager.model.BrandEntity;
import org.workshop.automanager.repository.BrandRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandMapper brandMapper;

    public void createBrand(BrandRequestDTO request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new AlreadyExistsException("Marca já cadastrada.");
        }
        brandRepository.save(brandMapper.toEntity(request));
    }

    public BrandresponseDTO getBrand(int id) {
        Optional<BrandEntity> entity = brandRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("Marca não encontrada.");
        }
        return brandMapper.toBrandResponseDTO(entity.get());
    }

    public List<BrandresponseDTO> getAll() {
        return brandMapper.toBrandResponseList(brandRepository.findAll());
    }

    public void update(BrandRequestDTO request, int id) {
        Optional<BrandEntity> entity = brandRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("Marca não encontrada.");
        }
        entity.get().setName(request.getName());
        brandRepository.save(entity.get());
    }

    public void delete(int id) {
        Optional<BrandEntity> entity = brandRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("Marca não encontrada.");
        }
        brandRepository.delete(entity.get());
    }
}