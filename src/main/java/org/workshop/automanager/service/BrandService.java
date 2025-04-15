package org.workshop.automanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.workshop.automanager.dto.request.BrandRequestDTO;
import org.workshop.automanager.dto.response.BrandresponseDTO;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.exception.InvalidArgumentException;
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
            throw new AlreadyExistsException("A marca " + request.getName() + " já está cadastrada.");
        }
        brandRepository.save(brandMapper.toEntity(request));
    }

    public BrandresponseDTO getBrand(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        Optional<BrandEntity> entity = brandRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("Marca não encontrada.");
        }
        return brandMapper.toBrandResponseDTO(entity.get());
    }

    public List<BrandresponseDTO> getAll() {
        return brandMapper.toBrandResponseList(brandRepository.findAll());
    }

    public void update(BrandRequestDTO request, Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        Optional<BrandEntity> entity = brandRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("Marca não encontrada.");
        }
        entity.get().setName(request.getName());
        brandRepository.save(entity.get());
    }

    public void delete(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        Optional<BrandEntity> entity = brandRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("Marca não encontrada.");
        }
        brandRepository.delete(entity.get());
    }

    public String getBrandNameById(Integer brandId) {
        if (brandId == null || brandId <= 0) {
            throw new InvalidArgumentException("O ID " + brandId + " recebido é inválido");
        }
        return brandRepository.findById(brandId)
                .map(BrandEntity::getName)
                .orElseThrow(() -> new NotFoundException("Não há marca com o ID " + brandId + " fornecido"));
    }
}