package com.company.customer;

// creating my class customer same as my file name
public class Customer {

    // used private to protect my vaRIABLES Encapsulation
    private String name;
    private String email;
    private String phone;



// initialising my constructor
    public Customer(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;


    }

    // Setters (assigning(setting) variables)
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }





    // Getters ( getting \ returnig variables)
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }


    // whats left next
    // i have shown encapsulation
    //inheritance left
    //overridea
    //abstraction, enums and records.
}