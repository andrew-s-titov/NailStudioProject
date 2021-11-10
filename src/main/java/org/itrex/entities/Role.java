package org.itrex.entities;

import lombok.*;
import org.itrex.entities.enums.RoleType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public String toString() {
        return roletype.name();
    }
}