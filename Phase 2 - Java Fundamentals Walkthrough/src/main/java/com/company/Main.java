package com.company;

import com.company.customer.Customer;
import com.company.customer.Address;
import com.company.loan.LoanProduct;
import com.company.loan.LoanApplication;
import com.company.loan.LoanStatus;
import com.company.payment.Repayment;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        LoanProduct product = new LoanProduct("Personal", 5.5, 12);
        LoanApplication application = new LoanApplication(customer, product, 5000.0);

        System.out.println("Customer: " + customer.getName());
        System.out.println("Loan: " + product.getName());
        System.out.println("Amount: $" + application.getAmount());
        System.out.println("Status: " + application.getStatus());
    }
}