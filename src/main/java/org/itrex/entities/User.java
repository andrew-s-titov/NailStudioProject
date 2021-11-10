package org.itrex.entities;

import lombok.*;
import org.itrex.entities.enums.Discount;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Record> records = new ArrayList<>();

    @ManyToMany
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

    public void addRecord(Record record) {
        records.add(record);
        record.setUser(this);
    }

    public void removeRecord(Record record) {
        records.remove(record);
    }

    @Override
    public String toString() {
        return "- - - User #" + userId + ": " +
                firstName + " " + lastName + ", " +
                phone + ", " + email +
                ", discount: " + discount + " - - -";
    }
}