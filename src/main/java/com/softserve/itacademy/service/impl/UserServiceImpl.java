package com.softserve.itacademy.service.impl;

import com.softserve.itacademy.exception.NullEntityReferenceException;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(User user) {
        if (user != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        throw new NullEntityReferenceException("User cannot be 'null'");
    }

    @Override
    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public User update(User user) {
        if (user != null) {
            readById(user.getId());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        throw new NullEntityReferenceException("User cannot be 'null'");
    }

    @Override
    public void delete(long id) {
        userRepository.delete(readById(id));
    }

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? new ArrayList<>() : users;
    }
 //   private final UserRepository userRepository;
//
//
//    public UserServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public User create(User user) {
//        if (user != null) {
//            user.setPassword(user.getPassword());
//            return userRepository.save(user);
//        }
//        throw new NullEntityReferenceException("User cannot be 'null'");
//    }
//
//    @Override
//    public User readById(long id) {
//        return userRepository.findById(id).orElseThrow(
//                () -> new EntityNotFoundException("User with id " + id + " not found"));
//    }
//
//    @Override
//    public User update(User user) {
//        if (user != null) {
//            readById(user.getId());
//            user.setPassword(user.getPassword());
//            return userRepository.save(user);
//        }
//        throw new NullEntityReferenceException("User cannot be 'null'");
//    }
//
//    @Override
//    public void delete(long id) {
//        userRepository.delete(readById(id));
//    }
//
//    @Override
//    public List<User> getAll() {
//        List<User> users = userRepository.findAll();
//        return users.isEmpty() ? new ArrayList<>() : users;
//    }
}
