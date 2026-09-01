package com.company.repository;

import com.company.model.LoanApplication;
import com.company.model.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {
    void deleteByLoanApplication(LoanApplication loanApplication);
}