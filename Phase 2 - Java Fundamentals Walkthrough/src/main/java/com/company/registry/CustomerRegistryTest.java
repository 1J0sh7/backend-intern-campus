package com.company.registry;

import com.company.customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerRegistryTest {
    private CustomerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new CustomerRegistry();
    }

    @Test
    void testAddCustomer() {
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        registry.addCustomer(customer);
        assertEquals(1, registry.getCount());
    }

    @Test
    void testAddDuplicateCustomerThrowsException() {
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        registry.addCustomer(customer);

        Customer duplicate = new Customer("John Doe", "john@email.com", "0987654321");
        assertThrows(DuplicateCustomerException.class, () -> registry.addCustomer(duplicate));
    }

    @Test
    void testFindByEmail() {
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        registry.addCustomer(customer);

        assertTrue(registry.findByEmail("john@email.com").isPresent());
        assertEquals("John Doe", registry.findByEmail("john@email.com").get().getName());
    }

    @Test
    void testFindByEmailNotFound() {
        assertFalse(registry.findByEmail("notfound@email.com").isPresent());
    }

    @Test
    void testDeleteByEmail() {
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        registry.addCustomer(customer);

        registry.deleteByEmail("john@email.com");
        assertEquals(0, registry.getCount());
    }

    @Test
    void testDeleteByEmailNotFoundThrowsException() {
        assertThrows(CustomerNotFoundException.class, () -> registry.deleteByEmail("notfound@email.com"));
    }

    @Test
    void testFindAll() {
        Customer c1 = new Customer("John Doe", "john@email.com", "1234567890");
        Customer c2 = new Customer("Jane Smith", "jane@email.com", "0987654321");
        registry.addCustomer(c1);
        registry.addCustomer(c2);

        assertEquals(2, registry.findAll().size());
    }
}