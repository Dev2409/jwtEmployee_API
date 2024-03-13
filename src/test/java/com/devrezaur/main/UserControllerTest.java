package com.devrezaur.main;

import com.devrezaur.main.controller.UserController2;
import com.devrezaur.main.model.User2;
import com.devrezaur.main.repository.UserRepository2;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository2 userRepository;

    @InjectMocks
    private UserController2 userController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetAllUsersEndpoint() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User2(1L, "John", "Doe", "john@example.com")));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    public void testGetUserByIdEndpoint() throws Exception {
        Long userId = 1L;
        User2 user = new User2(userId, "John", "Doe", "john@example.com");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    // Add similar tests for other endpoints like deleteUser, createUser, updateUser
    // ...

    @Test
    public void testDeleteUserEndpoint() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("User with ID 1 deleted successfully"));

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testCreateUserEndpoint() throws Exception {
        User2 user = new User2(1L, "John", "Doe", "john@example.com");

        // Configure the mock userRepository to capture the User2 object when save() is called
        ArgumentCaptor<User2> userCaptor = ArgumentCaptor.forClass(User2.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(user);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User created successfully"));

        // Verify that userRepository.save() was called with the same instance of User2
        verify(userRepository, times(1)).save(any(User2.class));

        // Get the captured User2 object
        User2 capturedUser = userCaptor.getValue();

        // Assert that the captured User2 object has the expected properties
        assertEquals(user.getFirstName(), capturedUser.getFirstName());
        assertEquals(user.getLastName(), capturedUser.getLastName());
        assertEquals(user.getEmail(), capturedUser.getEmail());
    }

    @Test
    public void testUpdateUserEndpoint() throws Exception {
        Long userId = 1L;
        User2 existingUser = new User2(userId, "John", "Doe", "john@example.com");
        User2 updatedUserDetails = new User2(userId, "Jane", "Doe", "jane@example.com");

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"email\":\"jane@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));

        verify(userRepository, times(1)).save(existingUser);
        assertEquals(updatedUserDetails.getFirstName(), existingUser.getFirstName());
        assertEquals(updatedUserDetails.getLastName(), existingUser.getLastName());
        assertEquals(updatedUserDetails.getEmail(), existingUser.getEmail());
    }

    @Test
    public void testUpdateUserNotFound() {
        // Arrange
        Long userId = 1L;
        User2 updatedUserDetails = new User2(userId, "Jane", "Doe", "jane@example.com");

        // Act
        try {
            userController.updateUser(userId, updatedUserDetails);
        } catch (UserNotFoundException e) {
            // Assert (Expecting 404 Not Found)
            return;
        }

        // Fail if no exception is thrown
        fail("Expected UserNotFoundException but no exception was thrown");
    }

}