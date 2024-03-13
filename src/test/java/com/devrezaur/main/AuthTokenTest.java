//package com.devrezaur.main;
//
//import com.devrezaur.main.controller.UserController2;
//import com.devrezaur.main.model.RefreshToken;
//import com.devrezaur.main.model.User2;
//import com.devrezaur.main.repository.UserRepository2;
//import org.junit.Before;
//import com.devrezaur.main.security.jwt.RefreshTokenService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Arrays;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@RunWith(MockitoJUnitRunner.class)
//public class AuthTokenTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private UserRepository2 userRepository;
//
//    @InjectMocks
//    private UserController2 userController;
//
//    @Before
//    public void init() {
//        MockitoAnnotations.initMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//    }
//
//    // Existing test methods for CRUD endpoints
//    // ...
//
//    @Test
//    public void testCreateAccessToken() throws Exception {
//        User2 user = new User2(1L, "John", "Doe", "john@example.com");
//
//        when(userRepository.save(user)).thenReturn(user);
//
//        mockMvc.perform(post("/auth/authenticate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"username\":\"john@example.com\",\"password\":\"password\"}"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.tokenType").value("Bearer"))
//                .andExpect(jsonPath("$.accessToken").isString())
//                .andExpect(jsonPath("$.refreshToken").isString())
//                .andExpect(jsonPath("$.userId").value(1))
//                .andExpect(jsonPath("$.fullname").value("John Doe"))
//                .andExpect(jsonPath("$.username").value("john@example.com"))
//                .andExpect(jsonPath("$.roles").isArray());
//    }
//
//    @Test
//    public void testRefreshAccessToken() throws Exception {
//        String refreshToken = "sample_refresh_token";
//
//        when(RefreshTokenService.findByRefreshToken(refreshToken)).thenReturn(new RefreshToken("sample_refresh_token"));
//
//        mockMvc.perform(post("/auth/refreshtoken")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"token\":\"" + refreshToken + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.tokenType").value("Bearer"))
//                .andExpect(jsonPath("$.accessToken").isString())
//                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
//    }
//
//    @Test
//    public void testLogoutUser() throws Exception {
//        Long userId = 1L;
//
//        mockMvc.perform(post("/auth/logout")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"id\":" + userId + "}"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User logged out"));
//
//        verify(RefreshTokenService, times(1)).deleteByUserId(userId);
//    }
//}