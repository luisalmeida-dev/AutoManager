package org.workshop.automanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.workshop.automanager.dto.request.UserRequestDTO;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.mapper.UserMapper;
import org.workshop.automanager.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public void createUser(UserRequestDTO request) {
        if(userRepository.existsByLogin(request.getLogin())) {
            throw new AlreadyExistsException("O usuário " + request.getLogin() + "já existe.");
        }
        userRepository.save(userMapper.toUserEntity(request));
    }
}