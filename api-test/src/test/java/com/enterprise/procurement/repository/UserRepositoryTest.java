package com.enterprise.procurement.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindAll() {
        System.out.println("Starting testFindAll...");
        var users = userRepository.findAll();
        System.out.println("Found " + users.size() + " users!");
    }
}
