package org.itrex.entities;

import org.itrex.entities.enums.RoleType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", schema = "public")
public class Role {

    @Id
    @Column(name = "role_id")
    private long roleId;

    @Column(name = "role_name", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RoleType roletype;

    @ManyToMany(mappedBy = "userRoles")
    private Set<User> users = new HashSet<>();

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public RoleType getRoletype() {
        return roletype;
    }

    public void setRoletype(RoleType roletype) {
        this.roletype = roletype;
    }

    public Set<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return roletype.name();
    }
}