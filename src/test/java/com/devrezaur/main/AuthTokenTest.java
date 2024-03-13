package com.devrezaur.main;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.devrezaur.main.repository.RefreshTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.devrezaur.main.controller.AuthController;
import com.devrezaur.main.model.RefreshToken;
import com.devrezaur.main.model.User;
import com.devrezaur.main.payload.JwtResponse;
import com.devrezaur.main.security.jwt.JwtUtils;
import com.devrezaur.main.security.jwt.RefreshTokenService;
import com.devrezaur.main.service.MyUserDetails;
import com.devrezaur.main.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class AuthTokenTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testAuthenticate_ValidCredentials() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("DevRezaur");
        user.setPassword("iamadmin");
        user.setFullname("Rezaur Rahman");

        Collection<? extends GrantedAuthority> authorities = new ArrayList<>(); // Initialize authorities list

        MyUserDetails userDetails = new MyUserDetails(user.getId(), user.getFullname(), user.getUsername(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authentication); // Return a valid authentication object

        final String jwtToken = "testJwtToken";
        final String refreshTokenValue = "testRefreshToken";

        when(jwtUtils.generateToken(userDetails)).thenReturn(jwtToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(refreshTokenValue);
        when(refreshTokenService.createRefreshToken(userDetails.getId())).thenReturn(refreshToken);

        // Act
        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.token").value(jwtToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshTokenValue));

        // Assert
        // You can add more assertions as needed
    }
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
