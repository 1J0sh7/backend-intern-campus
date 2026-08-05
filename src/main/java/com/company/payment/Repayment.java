package com.company.payment;

import com.company.loan.LoanApplication;

public class Repayment {
    private LoanApplication loanApplication;
    private double amount;
    private String dueDate;
    private String paidDate;

    public Repayment(LoanApplication loanApplication, double amount, String dueDate) {
        this.loanApplication = loanApplication;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paidDate = null;
    }

    public void setLoanApplication(LoanApplication loanApplication) { this.loanApplication = loanApplication; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setPaidDate(String paidDate) { this.paidDate = paidDate; }

    public LoanApplication getLoanApplication() { return loanApplication; }
    public double getAmount() { return amount; }
    public String getDueDate() { return dueDate; }
    public String getPaidDate() { return paidDate; }
}