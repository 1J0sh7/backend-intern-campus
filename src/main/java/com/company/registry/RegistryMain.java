package com.company.registry;

import com.company.customer.Customer;

public class RegistryMain {
    public static void main(String[] args) {
        CustomerRegistry registry = new CustomerRegistry();

        // Add customers
        Customer c1 = new Customer("John Doe", "john@email.com", "1234567890");
        Customer c2 = new Customer("Jane Smith", "jane@email.com", "0987654321");
        Customer c3 = new Customer("Alice Brown", "alice@email.com", "1122334455");

        registry.addCustomer(c1);
        registry.addCustomer(c2);
        registry.addCustomer(c3);

        System.out.println("All customers:");
        registry.findAll().forEach(c -> System.out.println(" - " + c.getName() + " (" + c.getEmail() + ")"));

        System.out.println("\nFind by email:");
        registry.findByEmail("john@email.com").ifPresent(c -> System.out.println("Found: " + c.getName()));

        System.out.println("\nTotal customers: " + registry.getCount());

        // Test delete
        registry.deleteByEmail("john@email.com");
        System.out.println("\nAfter deleting John: " + registry.getCount() + " customers left");
    }
}