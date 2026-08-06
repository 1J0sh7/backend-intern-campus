package com.company.service;

import com.company.model.Customer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CustomerService {

    private List<Customer> customers = new ArrayList<>();
    private AtomicLong idCounter = new AtomicLong(1);

    // Get all customers
    public List<Customer> getAllCustomers() {
        return customers;
    }

    // Get customer by ID
    public Customer getCustomerById(Long id) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    // Create a new customer
    public Customer createCustomer(Customer customer) {
        customer.setId(idCounter.getAndIncrement());
        customers.add(customer);
        return customer;
    }

    // Update an existing customer
    public Customer updateCustomer(Long id, Customer updated) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) {
                c.setName(updated.getName());
                c.setEmail(updated.getEmail());
                c.setPhone(updated.getPhone());
                return c;
            }
        }
        return null;
    }

    // Delete a customer
    public boolean deleteCustomer(Long id) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) {
                customers.remove(c);
                return true;
            }
        }
        return false;
    }
}