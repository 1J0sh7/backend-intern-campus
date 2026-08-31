package com.company.controller;
import com.company.model.LoanProduct;
import com.company.service.LoanProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/api/v1/loan-products")
@Tag(name = "Loan Products", description = "Loan product management endpoints")
public class LoanProductController {
    private final LoanProductService loanProductService;
    public LoanProductController(LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Create loan product", description = "Admin-only. Creates a new loan product with its terms and conditions.")
    public ResponseEntity<LoanProduct> createLoanProduct(@Valid @RequestBody LoanProduct product) {
        return new ResponseEntity<>(loanProductService.createLoanProduct(product), HttpStatus.CREATED);
    }
    @GetMapping
    @Operation(summary = "[PUBLIC] List loan products", description = "Returns all available loan products. No admin role required.")
    public ResponseEntity<List<LoanProduct>> getAllLoanProducts() {
        return ResponseEntity.ok(loanProductService.getAllLoanProducts());
    }
    @GetMapping("/{id}")
    @Operation(summary = "[PUBLIC] Get loan product by ID", description = "Returns a single loan product, or an error if no product exists with the given ID. No admin role required.")
    public ResponseEntity<LoanProduct> getLoanProductById(@PathVariable Long id) {
        return ResponseEntity.ok(loanProductService.getLoanProductById(id));
    }
}