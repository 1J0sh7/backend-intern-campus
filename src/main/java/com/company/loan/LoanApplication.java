package com.company.loan;

import com.company.customer.Customer;

public class LoanApplication {
    private Customer customer;
    private LoanProduct product;
    private double amount;
    private LoanStatus status;

    public LoanApplication(Customer customer, LoanProduct product, double amount) {
        this.customer = customer;
        this.product = product;
        this.amount = amount;
        this.status = LoanStatus.PENDING;
    }

    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setProduct(LoanProduct product) { this.product = product; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public Customer getCustomer() { return customer; }
    public LoanProduct getProduct() { return product; }
    public double getAmount() { return amount; }
    public LoanStatus getStatus() { return status; }
}