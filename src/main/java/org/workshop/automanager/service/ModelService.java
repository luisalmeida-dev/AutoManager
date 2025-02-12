package org.workshop.automanager.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.workshop.automanager.dto.request.ModelRequestDTO;
import org.workshop.automanager.dto.response.ModelResponseDTO;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.exception.NotFoundException;
import org.workshop.automanager.mapper.ModelMapper;
import org.workshop.automanager.model.ModelEntity;
import org.workshop.automanager.repository.ModelRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ModelService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private BrandService brandService;


    public void create(ModelRequestDTO request) {
        if (modelRepository.existsByNameAndBrandId(request.getName(), request.getBrandId())) {
            throw new AlreadyExistsException("O modelo " + request.getName() + " da marca " + brandService.getBrandNameById(request.getBrandId()) + " já existe.");
        }
        doesBrandExists(request.getBrandId());
        ModelEntity entity = modelMapper.toModelEntity(request);
        modelRepository.save(entity);
    }

    public ModelResponseDTO getById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        ModelEntity entity = modelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Modelo com ID " + id + " não encontrado"));
        return modelMapper.toModelResponseDTO(entity, brandService);
    }

    public List<ModelResponseDTO> getAll() {
        return modelMapper.toModelResponseDTOList(modelRepository.findAll(), brandService);
    }

    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        Optional<ModelEntity> entity = modelRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("O modelo nao foi encontrado.");
        }
        modelRepository.delete(entity.get());
    }

    public void update(@Valid ModelRequestDTO request, Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        doesBrandExists(request.getBrandId());
        Optional<ModelEntity> entity = modelRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("O modelo não foi encontrado.");
        }
        entity.get().setName(request.getName());
        entity.get().setBrandId(request.getBrandId());
        modelRepository.save(entity.get());
    }

    private void doesBrandExists(Integer id) {
        if (brandService.getBrand(id) == null) {
            throw new NotFoundException("Marca nao encontrada.");
        }
    }
}