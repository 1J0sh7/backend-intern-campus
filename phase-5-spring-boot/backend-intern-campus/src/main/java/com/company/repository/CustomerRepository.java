package com.company.repository;

import com.company.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // NEW: Search methods
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<Customer> findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);
}