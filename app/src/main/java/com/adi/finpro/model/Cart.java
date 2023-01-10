package com.adi.finpro.model;

import java.util.List;

public class Cart implements Cloneable {
    private String email;
    private List<OrderDetail> cartDetails;
    private String key;

    public Cart() {

    }

    public Cart(String email, List<OrderDetail> cartDetails) {
        this.email = email;
        this.cartDetails = cartDetails;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<OrderDetail> getCartDetails() {
        return cartDetails;
    }

    public void setCartDetails(List<OrderDetail> cartDetails) {
        this.cartDetails = cartDetails;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public Cart clone() {
        try {
            Cart clone = (Cart) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
