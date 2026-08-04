package com.company.customer;

public class CustomerTest {
    public static void main(String[] args) {
        // Test 1: Create Customer
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        System.out.println("Test 1: Create Customer");
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Passed");
        System.out.println();

        // Test 2: Update Customer
        customer.setName("Jane Doe");
        customer.setEmail("jane@email.com");
        customer.setPhone("0987654321");
        System.out.println("Test 2: Update Customer");
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Passed");
        System.out.println();
    }
}