package com.devrezaur.main;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devrezaur.main.model.Role;
import com.devrezaur.main.model.User;
import com.devrezaur.main.repository.RoleRepository;
import com.devrezaur.main.repository.UserRepository;
import com.devrezaur.main.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Before
    public void setUp() {
        when(roleRepository.findByRole("ROLE_USER")).thenReturn(new Role(1, "ROLE_USER"));
        when(roleRepository.findByRole("ROLE_ADMIN")).thenReturn(new Role(2, "ROLE_ADMIN"));
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertEquals("testUser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(1, savedUser.getRoles().size()); // One role should be assigned to the user
        assertEquals("ROLE_USER", savedUser.getRoles().get(0).getRole());
    }

    @Test
    public void testSaveAdmin() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("adminPassword");

        when(passwordEncoder.encode("adminPassword")).thenReturn("encodedAdminPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedAdmin = userService.saveAdmin(user);

        assertEquals("admin", savedAdmin.getUsername());
        assertEquals("encodedAdminPassword", savedAdmin.getPassword());
        assertEquals(1, savedAdmin.getRoles().size()); // One role should be assigned to the admin
        assertEquals("ROLE_ADMIN", savedAdmin.getRoles().get(0).getRole());
    }


}
