package org.itrex.entity;

import lombok.*;
import org.itrex.entity.enums.RoleType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "roles", schema = "public")
public class Role {

    @Id
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RoleType roleType;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "userRoles")
    private Set<User> users = new HashSet<>();

    @Builder
    public Role(Integer roleId, RoleType roleType) {
        this.roleId = roleId;
        this.roleType = roleType;
    }

    @Override
    public String toString() {
        return roleType.name();
    }
}