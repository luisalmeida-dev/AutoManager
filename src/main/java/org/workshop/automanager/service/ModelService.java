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


    public void create (ModelRequestDTO request) {
        if(modelRepository.existsByNameAndBrandId(request.getName(), request.getBrandId())) {
            throw new AlreadyExistsException("O modelo " + request.getName() + " da marca " + brandService.getBrandNameById(request.getBrandId()) + " já existe.");
        }
        doesBrandExists(request.getBrandId());
        ModelEntity entity = modelMapper.toModelEntity(request);
        modelRepository.save(entity);
    }

    public ModelResponseDTO getById(Integer id) {
        Optional<ModelEntity> entity = modelRepository.findById(id);
        if(entity.isEmpty()) {
            throw new NotFoundException("O modelo não foi encontrado.");
        }
        brandService.getBrand(entity.get().getBrandId());
        return modelMapper.toModelResponseDTO(entity.get(), brandService);
    }

    public List<ModelResponseDTO> getAll() {
        return modelMapper.toModelResponseDTOList(modelRepository.findAll(), brandService);
    }

    public void deleteById(Integer id) {
        Optional<ModelEntity> entity = modelRepository.findById(id);
        if(entity.isEmpty()) {
            throw new NotFoundException("O modelo nao foi encontrado.");
        }
        modelRepository.delete(entity.get());
    }

    public void update(@Valid ModelRequestDTO request, Integer id) {
        doesBrandExists(request.getBrandId());
        Optional<ModelEntity> entity = modelRepository.findById(id);
        if(entity.isEmpty()) {
            throw new NotFoundException("O modelo não foi encontrado.");
        }
        entity.get().setName(request.getName());
        entity.get().setBrandId(request.getBrandId());
        modelRepository.save(entity.get());
    }

    private void doesBrandExists(Integer id) {
        if(brandService.getBrand(id) == null) {
            throw new NotFoundException("Marca nao encontrada.");
        };
    }
}