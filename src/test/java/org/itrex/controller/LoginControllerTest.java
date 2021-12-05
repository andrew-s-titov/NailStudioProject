package org.itrex.controller;

import org.itrex.dto.UserLoginRequestDTO;
import org.itrex.entity.User;
import org.itrex.repository.data.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginControllerTest extends BaseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_PHONE = "+375293000000";
    private static final String TEST_PASSWORD = "+375293000000";

    @Test
    @DisplayName("login with valid credentials - should return OK status, create auth token")
    public void login1() throws Exception {
        // given
        User user = userWithTestPhoneNumber();
        when(userRepository.findByPhone(TEST_PHONE)).thenReturn(Optional.of(user));

        // when & then
        mockMvc.perform(post("/login")
                .content(toJson(new UserLoginRequestDTO(TEST_PHONE, TEST_PASSWORD)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("AUTHORIZATION"));
    }

    @Test
    @DisplayName("login with wrong credentials - should return 401 status")
    public void login2() throws Exception {
        // given
        User user = userWithTestPhoneNumber();
        when(userRepository.findByPhone(TEST_PHONE)).thenReturn(Optional.of(user));

        // when & then
        mockMvc.perform(post("/login")
                .content(toJson(new UserLoginRequestDTO(TEST_PHONE, "wrongPass")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    public User userWithTestPhoneNumber() {
        return User.builder()
                .phone(TEST_PHONE)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .build();
    }
}
