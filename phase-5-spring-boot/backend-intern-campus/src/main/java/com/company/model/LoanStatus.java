package com.company.model;

public enum LoanStatus {
    PENDING,     // Awaiting admin decision
    APPROVED,    // Admin approved, waiting for disbursement
    REJECTED,    // Admin rejected
    DISBURSED,   // Money sent to customer
    ACTIVE,      // Loan is active (repayments ongoing)
    COMPLETED    // All repayments done
}