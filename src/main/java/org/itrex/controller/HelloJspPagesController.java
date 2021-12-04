package org.itrex.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itrex.dto.UserCreditsDTO;
import org.itrex.dto.UserLoginRequestDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.service.UserService;
import org.itrex.util.PasswordEncryption;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Controller
@SessionAttributes("user")
public class HelloJspPagesController {

//    private final UserService userService;

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /*
    @PostMapping( "/login")
    public String login(Model model, @Valid @RequestBody UserLoginRequestDTO user) throws DatabaseEntryNotFoundException {

        // TODO: minimize logic, transfer to service
        String phone = user.getPhone();
        UserCreditsDTO userByPhone = userService.getUserByPhone(phone);
        if (userByPhone == null) {
            String message = String.format("User with this login (phone number %s) not found!", phone);
            log.trace(message);
            throw new BadCredentialsException(message);
        }
        byte[] password = userByPhone.getPassword();
        if (PasswordEncryption.authenticate(user.getPassword(), password)) {
            UserResponseDTO userById = userService.getUserById(userByPhone.getUserId());
            model.addAttribute("user", userById);
            return "redirect:profile";
        } else {
            String message = String.format("Wrong password for login %s", phone);
            log.trace(message);
            throw new BadCredentialsException(message);
        }
    }
    */
}