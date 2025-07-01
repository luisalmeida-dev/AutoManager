package org.workshop.automanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.workshop.automanager.dto.request.UpdatePasswordRequestDTO;
import org.workshop.automanager.dto.request.UserRequestDTO;
import org.workshop.automanager.dto.response.UserResponseDTO;
import org.workshop.automanager.enums.RoleEnum;
import org.workshop.automanager.exception.AlreadyExistsException;
import org.workshop.automanager.exception.InvalidArgumentException;
import org.workshop.automanager.exception.NotFoundException;
import org.workshop.automanager.mapper.UserMapper;
import org.workshop.automanager.model.UserEntity;
import org.workshop.automanager.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public void createUser(UserRequestDTO request) {
        if(request.getRole() == RoleEnum.CUSTOMER){
            throw new InvalidArgumentException("O Cargo " + request.getRole() + " recebido é inválido");
        } else if(userRepository.existsByLogin(request.getLogin())) {
            throw new AlreadyExistsException("O usuário " + request.getLogin() + "já existe.");
        }
        userRepository.save(userMapper.toUserEntity(request));
    }

    public UserResponseDTO getUserById(Integer id) {
        if(id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new NotFoundException("O usuário não foi encontrado."));
        return userMapper.toUserResponseDTO(userEntity);
    }

    public List<UserResponseDTO> getAll() {
        return userMapper.toUserResponseDTOList(userRepository.findAll());
    }

    public UserResponseDTO updateUserById(Integer id, UserRequestDTO request) {
        if(id == null || id <= 0) {
            throw new InvalidArgumentException("O Id " + id + " recebido é inválido");
        }
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new NotFoundException("O usuário não foi encontrado."));
        if(userEntity == null) {
            throw new NotFoundException("Cliente não encontrado!");
        }

        userEntity.setLogin(request.getLogin());
        userEntity.setName(request.getName());
        userEntity.setRole(request.getRole());
        userRepository.save(userEntity);

        return userMapper.toUserResponseDTO(userEntity);
    }

    public void updatePassword(UpdatePasswordRequestDTO request) {
        if(request.getId() == null || request.getId() <= 0) {
            throw new InvalidArgumentException("O ID " + request.getId() + " recebido é inválido");
        }
        UserEntity user = userRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("O usuário não foi encontrado."));

        validatePassword(request.getNewPassword());
        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new InvalidArgumentException("A senha antiga está incorreta.");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }

    public void deleteUserById(Integer id) {
        if(id == null || id <= 0) {
            throw new InvalidArgumentException("O ID " + id + " recebido é inválido");
        }
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("O usuário com o ID " + id + " não foi encontrado.");
        }
        userRepository.deleteById(id);
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidArgumentException("A senha não pode ser nula ou vazia.");
        }

        List<String> validationErrors = new ArrayList<>();

        if (password.length() < 8) {
            validationErrors.add("a senha deve ter pelo menos 8 caracteres");
        }

        //Validação de Complexidade (usando Regex)
        if (!password.matches(".*[A-Z].*")) {
            validationErrors.add("a senha deve conter pelo menos uma letra maiúscula");
        }
        if (!password.matches(".*[a-z].*")) {
            validationErrors.add("a senha deve conter pelo menos uma letra minúscula");
        }
        if (!password.matches(".*\\d.*")) {
            validationErrors.add("a senha deve conter pelo menos um número");
        }
        if (!password.matches("^(?=.*[^a-zA-Z0-9]).*$")) {
            validationErrors.add("a senha deve conter pelo menos um caractere especial");
        }

        if (!validationErrors.isEmpty()) {
            String message = "A senha é inválida: " + String.join(", ", validationErrors) + ".";
            throw new InvalidArgumentException(message);
        }
    }
}