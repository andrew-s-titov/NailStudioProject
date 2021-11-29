package org.itrex.controller;

import lombok.AllArgsConstructor;
import org.itrex.dto.RoleDTO;
import org.itrex.exception.DatabaseEntryNotFoundException;
import org.itrex.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("roles")
public class RoleController {
    private final RoleService roleService;

    @GetMapping("")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> allRoles = roleService.getRoles();
        return new ResponseEntity<>(allRoles, HttpStatus.OK);
    }

    @GetMapping("/get/name/{roleName}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String roleName) throws DatabaseEntryNotFoundException {
        RoleDTO role = roleService.getRoleByName(roleName);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @GetMapping("/get/id/{roleId}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Integer roleId) throws DatabaseEntryNotFoundException {
        RoleDTO role = roleService.getRoleById(roleId);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}