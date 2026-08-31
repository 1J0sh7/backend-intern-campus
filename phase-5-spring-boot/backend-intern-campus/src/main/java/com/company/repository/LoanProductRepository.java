package com.company.repository;

import com.company.model.LoanProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    // You can add this method if you want to filter by active products only
    Page<LoanProduct> findByActiveTrue(Pageable pageable);
}