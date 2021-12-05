package org.itrex.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.itrex.converter.impl.UserDTOConverterImpl;
import org.itrex.dto.UserLoginRequestDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.entity.User;
import org.itrex.security.JwtTokenUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Tag(name = "authentication")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDTOConverterImpl userDTOConverter;

    @PostMapping(value = "/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody @Valid UserLoginRequestDTO loginRequest)
            throws BadCredentialsException {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getPhone(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAccessToken(user))
                .body(userDTOConverter.toUserResponseDTO(user));
    }

    @PostMapping("logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, null);
    }
}