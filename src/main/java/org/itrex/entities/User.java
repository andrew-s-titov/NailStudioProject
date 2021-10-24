package org.itrex.entities;

import org.itrex.entities.enums.Discount;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Record> records = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "users_roles", schema = "public",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> userRoles = new HashSet<>();

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public Set<Role> getUserRoles() {
        return userRoles;
    }

    @Override
    public String toString() {
        return "- - - User #" + userId + ": " +
                firstName + " " + lastName + ", " +
                phone + ", " + email +
                ", discount: " + discount +
                ", roles: " + userRoles + " - - -";
    }
}