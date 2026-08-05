package com.company.registry;

import com.company.customer.Customer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRegistry {
    private List<Customer> customers;

    public CustomerRegistry() {
        this.customers = new ArrayList<>();
    }


    public void addCustomer(Customer customer) {
        for (Customer c : customers) {
            if (c.getEmail().equals(customer.getEmail())) {
                throw new DuplicateCustomerException("Email already exists: " + customer.getEmail());
            }
        }
        customers.add(customer);
    }



    // search and find customers


    public Optional<Customer> findByEmail(String email) {
        for (Customer c : customers) {
            if (c.getEmail().equals(email)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    // all customers
    public List<Customer> findAll() {
        return customers;
    }



    // Delete customer by email
    public void deleteByEmail(String email) {
        Customer toRemove = null;
        for (Customer c : customers) {
            if (c.getEmail().equals(email)) {
                toRemove = c;
                break;
            }
        }
        if (toRemove == null) {
            throw new CustomerNotFoundException("Customer not found: " + email);
        }
        customers.remove(toRemove);
    }

    // getting customers
    public int getCount() {
        return customers.size();
    }
}