package org.itrex.controller;

import lombok.RequiredArgsConstructor;
import org.itrex.dto.RoleDTO;
import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.dto.UserUpdateDTO;
import org.itrex.entity.enums.Discount;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.exception.DeletingClientWithActiveRecordsException;
import org.itrex.exception.RoleManagementException;
import org.itrex.exception.UserExistsException;
import org.itrex.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/get/all")
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> allUsers = userService.getAll();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) throws DatabaseEntryNotFoundException {
        UserResponseDTO user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO user) throws UserExistsException {
        UserResponseDTO createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // TODO: restrict use - only for admin
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) throws DeletingClientWithActiveRecordsException, DatabaseEntryNotFoundException {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUserInfo(@Valid @RequestBody UserUpdateDTO userUpdateDTO) throws DatabaseEntryNotFoundException {
        userService.updateUserInfo(userUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // TODO: restrict use - only for admin and staff
    @PutMapping("/change_discount")
    public ResponseEntity<Object> changeClientDiscount(@RequestParam Long clientId, @RequestParam Discount newDiscount) throws DatabaseEntryNotFoundException {
        userService.changeClientDiscount(clientId, newDiscount);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // TODO: restrict use - only for admin
    @PostMapping("/add_role")
    public ResponseEntity<Object> addRoleForUser(@RequestParam Long userId, @RequestBody RoleDTO roleDTO) throws RoleManagementException, DatabaseEntryNotFoundException {
        userService.addRoleForUser(userId, roleDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: restrict use - only for admin
    @DeleteMapping("/revoke_role")
    public ResponseEntity<Object> revokeRole(@RequestParam Long userId, @RequestBody RoleDTO roleDTO) throws RoleManagementException, DatabaseEntryNotFoundException {
        userService.revokeRole(userId, roleDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}