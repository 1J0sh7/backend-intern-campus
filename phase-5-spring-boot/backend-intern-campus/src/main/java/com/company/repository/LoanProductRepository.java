package com.company.repository;

import com.company.model.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    List<LoanProduct> findByActiveTrue();
}