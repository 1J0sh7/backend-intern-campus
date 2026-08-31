package com.company.controller;

import com.company.dto.LoanApplicationResponseDTO;
import com.company.model.Repayment;
import com.company.service.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loan-applications")
@Tag(name = "Loan Applications", description = "Loan application, approval, disbursement, and repayment endpoints")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    // 1. Apply for a loan (USER)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "[USER] Apply for a loan", description = "Submits a new loan application for the given customer and loan product, starting in PENDING status.")
    public ResponseEntity<LoanApplicationResponseDTO> applyForLoan(
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam Double amount) {
        return new ResponseEntity<>(
                loanApplicationService.applyForLoan(customerId, productId, amount),
                HttpStatus.CREATED
        );
    }

    // 2. Get a specific loan application (USER can view their own, ADMIN can view any)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "[USER/ADMIN] Get loan application by ID", description = "Returns a single loan application. Users can view their own applications; admins can view any.")
    public ResponseEntity<LoanApplicationResponseDTO> getApplication(@PathVariable Long id) {
        return ResponseEntity.ok(loanApplicationService.getApplicationById(id));
    }

    // 3. Get all applications for a specific customer (USER) — WITH PAGINATION
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "[USER] List applications by customer", description = "Returns paginated loan applications submitted by the given customer.")
    public ResponseEntity<Page<LoanApplicationResponseDTO>> getApplicationsByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(loanApplicationService.getApplicationsByCustomer(customerId, pageable));
    }

    // 4. Admin: Approve a loan
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Approve a loan application", description = "Admin-only. Approves a pending loan application and records the approving admin.")
    public ResponseEntity<LoanApplicationResponseDTO> approveLoan(@PathVariable Long id, Authentication authentication) {
        String adminUsername = authentication.getName();
        return ResponseEntity.ok(loanApplicationService.approveLoan(id, adminUsername));
    }

    // 5. Admin: Reject a loan (with reason)
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Reject a loan application", description = "Admin-only. Rejects a pending loan application with a given reason.")
    public ResponseEntity<LoanApplicationResponseDTO> rejectLoan(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(loanApplicationService.rejectLoan(id, reason));
    }

    // 6. Admin: Disburse a loan
    @PutMapping("/{id}/disburse")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Disburse a loan", description = "Admin-only. Marks an approved loan as disbursed, activating it for repayment.")
    public ResponseEntity<LoanApplicationResponseDTO> disburseLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanApplicationService.disburseLoan(id));
    }

    // 7. Make a repayment (USER)
    @PostMapping("/{id}/repayments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "[USER] Make a loan repayment", description = "Records a repayment against the given loan application and reduces its outstanding balance.")
    public ResponseEntity<Repayment> makeRepayment(
            @PathVariable Long id,
            @RequestParam Double amount) {
        return new ResponseEntity<>(
                loanApplicationService.makeRepayment(id, amount),
                HttpStatus.CREATED
        );
    }

    // 8. Admin: Get all applications (oversight) — WITH PAGINATION
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] List all loan applications", description = "Admin-only. Returns paginated list of every loan application in the system for oversight.")
    public ResponseEntity<Page<LoanApplicationResponseDTO>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(loanApplicationService.getAllApplications(pageable));
    }
}