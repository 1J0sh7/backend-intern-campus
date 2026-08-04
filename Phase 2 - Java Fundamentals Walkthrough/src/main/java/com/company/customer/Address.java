package com.company.customer;

public class Address extends Customer {
    private String street;
    private String city;


    public Address(String name, String email, String phone, String street, String city) {
        super(name, email, phone);
        this.street = street;
        this.city = city;

    }

    // Setter
    public void setStreet(String street) {
        this.street = street;
    }
    public void setCity(String city) {
        this.city = city;
    }

    // Getter
    public String getStreet() {
        return street;
    }
    public String getCity() {
        return city;
    }


    public void Display() {
        System.out.println("Customer: " + getName());
        System.out.println("Address: " + street + ", " + city);
    }
}