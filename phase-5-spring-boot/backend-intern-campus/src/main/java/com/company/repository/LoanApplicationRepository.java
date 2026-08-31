package com.company.repository;

import com.company.model.LoanApplication;
import com.company.model.Customer;
import com.company.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByCustomer(Customer customer);
    List<LoanApplication> findByStatus(LoanStatus status);
}