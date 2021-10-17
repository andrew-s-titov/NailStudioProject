package org.itrex.entities;

public class Role {
    /*
    Won't it be more sufficient to use ENUM for roles? With params.
    It's not going to be changed a lot
     */

    private long roleId;
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
