package org.itrex.entities;

import javax.persistence.*;

@Entity
@Table(name = "roles", schema = "public")
public class Role {
    /*
    Won't it be more sufficient to use ENUM for roles? With params.
    It's not going to be changed a lot
     */
    @Id
    @Column(name = "role_id")
    private long roleId;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
