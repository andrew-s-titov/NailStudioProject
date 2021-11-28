package org.itrex.entity;

import lombok.*;
import org.itrex.entity.enums.Discount;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "password", nullable = false)
    private byte[] password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "e_mail")
    private String email;

    @Column(name = "discount")
    @Enumerated(value = EnumType.STRING)
    private Discount discount = Discount.ZERO;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Record> clientRecords = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", schema = "public",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> userRoles = new HashSet<>();

    @Builder
    public User(Long userId, byte[] password, String firstName, String lastName, String phone, String email) {
        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public String toString() {
        return "- - - User #" + userId + ": " +
                firstName + " " + lastName + ", " +
                phone + ", " + email +
                ", discount: " + discount + " - - -";
    }
}