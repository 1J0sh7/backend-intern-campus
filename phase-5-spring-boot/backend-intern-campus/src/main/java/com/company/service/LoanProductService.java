package com.company.service;

import com.company.exception.ValidationException;
import com.company.model.LoanProduct;
import com.company.repository.LoanProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanProductService {

    private final LoanProductRepository loanProductRepository;

    public LoanProductService(LoanProductRepository loanProductRepository) {
        this.loanProductRepository = loanProductRepository;
    }

    // 1. Create a loan product (with validation)
    public LoanProduct createLoanProduct(LoanProduct product) {
        // Enforce 12-month maximum
        if (product.getTermMonths() > 12) {
            throw new ValidationException("Loan term cannot exceed 12 months");
        }

        // Enforce minimum 1 month
        if (product.getTermMonths() < 1) {
            throw new ValidationException("Loan term must be at least 1 month");
        }

        return loanProductRepository.save(product);
    }

    // 2. Get all loan products
    public List<LoanProduct> getAllLoanProducts() {
        return loanProductRepository.findAll();
    }

    // 3. Get loan product by ID
    public LoanProduct getLoanProductById(Long id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan product not found with id: " + id));
    }

    // 4. Update loan product (with validation)
    public LoanProduct updateLoanProduct(Long id, LoanProduct updatedProduct) {
        LoanProduct existing = getLoanProductById(id);

        // Enforce 12-month maximum
        if (updatedProduct.getTermMonths() > 12) {
            throw new ValidationException("Loan term cannot exceed 12 months");
        }

        if (updatedProduct.getTermMonths() < 1) {
            throw new ValidationException("Loan term must be at least 1 month");
        }

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setInterestRate(updatedProduct.getInterestRate());
        existing.setTermMonths(updatedProduct.getTermMonths());
        existing.setMaxAmount(updatedProduct.getMaxAmount());
        existing.setActive(updatedProduct.getActive());

        return loanProductRepository.save(existing);
    }

    // 5. Delete loan product (soft delete)
    public void deleteLoanProduct(Long id) {
        LoanProduct product = getLoanProductById(id);
        product.setActive(false);
        loanProductRepository.save(product);
    }

    // 6. Get active loan products only
    public List<LoanProduct> getActiveLoanProducts() {
        return loanProductRepository.findByActiveTrue();
    }
}