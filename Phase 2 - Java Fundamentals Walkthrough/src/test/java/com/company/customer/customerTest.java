package com.company.customer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    @Test
    void testCustomerCreation() {
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        assertEquals("John Doe", customer.getName());
        assertEquals("john@email.com", customer.getEmail());
        assertEquals("1234567890", customer.getPhone());
    }

    @Test
    void testCustomerSetters() {
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        customer.setName("Jane Doe");
        customer.setEmail("jane@email.com");
        customer.setPhone("0987654321");
        assertEquals("Jane Doe", customer.getName());
        assertEquals("jane@email.com", customer.getEmail());
        assertEquals("0987654321", customer.getPhone());
    }
}