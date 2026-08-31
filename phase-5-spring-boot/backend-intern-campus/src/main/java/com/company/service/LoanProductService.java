package com.company.service;

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

    public LoanProduct createLoanProduct(LoanProduct product) {
        return loanProductRepository.save(product);
    }

    public List<LoanProduct> getAllLoanProducts() {
        return loanProductRepository.findAll();
    }

    public LoanProduct getLoanProductById(Long id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan product not found"));
    }
}