package org.workshop.automanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.workshop.automanager.dto.request.CarRequestDTO;
import org.workshop.automanager.dto.response.CarResponseDTO;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.mapper.CarMapper;
import org.workshop.automanager.model.CarEntity;
import org.workshop.automanager.model.CustomerEntity;
import org.workshop.automanager.model.ModelEntity;
import org.workshop.automanager.repository.CarRepository;

import java.util.List;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private CarMapper carMapper;

    public void create(CarRequestDTO request) {
        if (carRepository.existsByPlate(request.getPlate())) {
            throw new AlreadyExistsException("O carro com placa " + request.getPlate() + " já existe");
        }
        ModelEntity model = modelService.getModelEntityById(request.getModelId());
        CustomerEntity customer = customerService.getCustomerEntityById(request.getCustomerId());

        carRepository.save(carMapper.toCarEntity(request, model, customer));
    }
}