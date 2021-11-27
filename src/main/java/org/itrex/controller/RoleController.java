package org.itrex.controller;

import lombok.AllArgsConstructor;
import org.itrex.dto.RoleDTO;
import org.itrex.service.RoleService;
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
    public List<RoleDTO> getAllRoles() {
        return roleService.getRoles();
    }

    @GetMapping("/get/name/{roleName}")
    RoleDTO getRoleByName(@PathVariable String roleName) {
        return roleService.getRoleByName(roleName);
    }

    @GetMapping("/get/id/{roleId}")
    RoleDTO getRoleById(@PathVariable Integer roleId) {
        return roleService.getRoleById(roleId);
    }
}