package com.company.loan;

public class LoanProduct {
    private String name;
    private double interestRate;
    private int termMonths;

// constructor initialising

    public LoanProduct(String name, double interestRate, int termMonths) {
        this.name = name;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
    }
// setters
    public void setName(String name) { this.name = name; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public void setTermMonths(int termMonths) { this.termMonths = termMonths; }

    //getters

    public String getName() { return name; }
    public double getInterestRate() { return interestRate; }
    public int getTermMonths() { return termMonths; }
}