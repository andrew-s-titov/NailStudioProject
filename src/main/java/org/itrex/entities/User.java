package org.itrex.entities;

import org.itrex.entities.enums.Discount;

public class User {
    private long userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Discount discount = Discount.ZERO;

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

    @Override
    public String toString() {
        return "- - - User #" + userId + ": " +
                firstName + " " + lastName + ", " +
                phone + ", " + email + ", " +
                "discount: " + discount + " - - -";
    }
}