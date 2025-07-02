package org.workshop.automanager.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.workshop.automanager.dto.request.ModelRequestDTO;
import org.workshop.automanager.dto.response.ModelResponseDTO;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.exception.InvalidArgumentException;
import org.workshop.automanager.exception.NotFoundException;
import org.workshop.automanager.mapper.ModelMapper;
import org.workshop.automanager.model.BrandEntity;
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
        BrandEntity brandEntity = brandService.getBrandEntityById(request.getBrandId());

        if (modelRepository.existsByNameAndBrand(request.getName(), brandEntity)) {
            throw new AlreadyExistsException("O modelo " + request.getName() + " da marca " + brandEntity.getName() + " já existe.");
        }
        ModelEntity entity = modelMapper.toModelEntity(request);
        entity.setBrand(brandEntity);
        modelRepository.save(entity);
    }

    public ModelResponseDTO getById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        ModelEntity entity = modelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Modelo com ID " + id + " não encontrado"));
        return modelMapper.toModelResponseDTO(entity);
    }


    public List<ModelResponseDTO> getAll() {
        return modelMapper.toModelResponseDTOList(modelRepository.findAll());
    }

    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        if (!modelRepository.existsById(id)) {
            throw new NotFoundException("Modelo com ID " + id + " não encontrado.");
        }
        modelRepository.deleteById(id);
    }

    public void update(@Valid ModelRequestDTO request, Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }

        ModelEntity modelEntity = modelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Modelo com ID " + id + " não encontrado."));

        BrandEntity brandEntity = brandService.getBrandEntityById(request.getBrandId());

        if (modelRepository.existsByNameAndBrand(request.getName(), brandEntity) && !modelEntity.getName().equals(request.getName())) {
            throw new AlreadyExistsException("O modelo " + request.getName() + " da marca " + brandEntity.getName() + " já existe.");
        }

        modelEntity.setName(request.getName());
        modelEntity.setBrand(brandEntity);
        modelRepository.save(modelEntity);
    }
}