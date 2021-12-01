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
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) throws DatabaseEntryNotFoundException {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO user) throws UserExistsException {
        return ResponseEntity.ok(userService.createUser(user));
    }

    // TODO: restrict use - only for admin
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) throws DeletingClientWithActiveRecordsException, DatabaseEntryNotFoundException {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUserInfo(@Valid @RequestBody UserUpdateDTO userUpdateDTO) throws DatabaseEntryNotFoundException {
        userService.updateUserInfo(userUpdateDTO);
        return ResponseEntity.noContent().build();
    }

    // TODO: restrict use - only for admin and staff
    @PutMapping("/change_discount")
    public ResponseEntity<Object> changeClientDiscount(@RequestParam Long clientId, @RequestParam Discount newDiscount) throws DatabaseEntryNotFoundException {
        userService.changeClientDiscount(clientId, newDiscount);
        return ResponseEntity.noContent().build();
    }

    // TODO: restrict use - only for admin
    @PostMapping("/add_role")
    public ResponseEntity<Object> addRoleForUser(@RequestParam Long userId, @RequestBody RoleDTO roleDTO) throws RoleManagementException, DatabaseEntryNotFoundException {
        userService.addRoleForUser(userId, roleDTO);
        return ResponseEntity.ok().build();
    }

    // TODO: restrict use - only for admin
    @DeleteMapping("/revoke_role")
    public ResponseEntity<Object> revokeRole(@RequestParam Long userId, @RequestBody RoleDTO roleDTO) throws RoleManagementException, DatabaseEntryNotFoundException {
        userService.revokeRole(userId, roleDTO);
        return ResponseEntity.ok().build();
    }
}