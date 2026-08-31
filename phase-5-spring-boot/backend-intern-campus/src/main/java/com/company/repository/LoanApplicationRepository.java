package com.company.repository;

import com.company.model.Customer;
import com.company.model.LoanApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    Page<LoanApplication> findByCustomer(Customer customer, Pageable pageable);
}