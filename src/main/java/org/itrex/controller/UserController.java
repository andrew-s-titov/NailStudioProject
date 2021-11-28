package org.itrex.controller;

import lombok.RequiredArgsConstructor;
import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entity.enums.Discount;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.RoleManagementException;
import org.itrex.exception.UserExistsException;
import org.itrex.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    // TODO: restrict use - only for admin and staff
    // TODO: pagination
    @GetMapping("get/all")
    public List<UserResponseDTO> getAll() {
        return userService.getAll();
    }

    @GetMapping("get/{userId}")
    public UserResponseDTO getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/create")
    public UserResponseDTO createUser(@Valid @RequestBody UserCreateDTO user) throws UserExistsException {
        return userService.createUser(user);
    }

    // TODO: restrict use - only for admin
    @DeleteMapping ("/delete/{userId}")
    public HttpStatus deleteUser(@PathVariable Long userId) throws DeletingClientWithActiveRecordsException {
        userService.deleteUser(userId);
        return HttpStatus.OK;
    }

    @PutMapping("/update")
    public HttpStatus updateUserInfo(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUserInfo(userUpdateDTO);
        return HttpStatus.OK;
    }

    // TODO: restrict use - only for admin and staff
    @PutMapping("/change_discount")
    public HttpStatus changeClientDiscount(@RequestParam Long clientId, @RequestParam Discount newDiscount) {
        userService.changeClientDiscount(clientId, newDiscount);
        return HttpStatus.OK;
    }

    // TODO: restrict use - only for admin
    @PostMapping("/add_role")
    public HttpStatus addRoleForUser(@RequestParam Long userId, @RequestParam String roleName) throws RoleManagementException {
        userService.addRoleForUser(userId, roleName);
        return HttpStatus.OK;
    }

    // TODO: restrict use - only for admin
    @DeleteMapping("/revoke_role")
    public HttpStatus revokeRole(@RequestParam Long userId, @RequestParam String roleName) throws RoleManagementException {
        userService.revokeRole(userId, roleName);
        return HttpStatus.OK;
    }
}