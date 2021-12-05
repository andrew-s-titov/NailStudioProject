package org.itrex;

import lombok.RequiredArgsConstructor;
import org.itrex.entity.Role;
import org.itrex.entity.User;
import org.itrex.entity.enums.RoleType;
import org.itrex.repository.data.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class FirstLaunchAdminCreation {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    private void createAdmin() {
        User admin = User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .email("admin@nailstudio.com")
                .phone("375291001010")
                .password(passwordEncoder.encode(password))
                .build();
        admin.getUserRoles().add(Role.builder().roleId(1).roleType(RoleType.ADMIN).build());
        userRepository.save(admin);
    }
}
