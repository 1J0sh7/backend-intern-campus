package com.company.loan;

import com.company.customer.Customer;

public class LoanApplicationTest {
    public static void main(String[] args) {
        // Test 1 Createing  LoanApplication
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        LoanProduct product = new LoanProduct("Personal", 5.5, 12);
        LoanApplication application = new LoanApplication(customer, product, 5000.0);

        System.out.println("Test Create LoanApplication");
        System.out.println("Customer: " + application.getCustomer().getName());
        System.out.println("Product: " + application.getProduct().getName());
        System.out.println("Amount: $" + application.getAmount());
        System.out.println("Status: " + application.getStatus());
        System.out.println("Passed");
        System.out.println();

        // Test 2  Update Status
        application.setStatus(LoanStatus.APPROVED);
        System.out.println("Test Update Status");
        System.out.println("New Status: " + application.getStatus());
        System.out.println("Passed");
        System.out.println();
    }
}