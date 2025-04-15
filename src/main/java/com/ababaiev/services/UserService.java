package com.ababaiev.services;

import com.ababaiev.exceptions.BadRequestException;
import com.ababaiev.models.Role;
import com.ababaiev.models.User;
import com.ababaiev.repositories.UserRepo;
import com.ababaiev.utils.CryptoUtils;
import com.ababaiev.views.signup.SignUpModel;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public byte[] signup(SignUpModel signUpModel) {
        if (userRepo.existsByUsername(signUpModel.getUsername())) {
            throw new BadRequestException("Username is already in use");
        }
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        String id = CryptoUtils.getHash64(publicKey);
        User user = new User();
        user.setUsername(signUpModel.getUsername());
        user.setPassword(passwordEncoder.encode(signUpModel.getPassword()));
        user.setPublicKey(publicKey);
        user.setRole(Role.ROLE_USER);
        user.setId(id);
        userRepo.save(user);
        return CryptoUtils.getAsFileContent(keyPair.getPrivate());

    }

    @Transactional
    public void addFunds(double amount) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username);
        user.setBalance(user.getBalance() + amount);
        userRepo.save(user);
    }

    public Double getFunds() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username).getBalance();
    }
}
