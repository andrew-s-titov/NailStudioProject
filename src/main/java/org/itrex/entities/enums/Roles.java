package org.itrex.entities.enums;

public enum Roles {
    ADMIN(101),
    MASTER(102),
    CLIENT(103);

    public final int roleId;

    Roles(int roleId) {
        this.roleId = roleId;
    }
}