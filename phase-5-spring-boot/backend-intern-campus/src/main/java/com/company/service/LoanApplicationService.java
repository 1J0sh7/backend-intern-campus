package com.company.service;

import com.company.dto.LoanApplicationResponseDTO;
import com.company.mapper.LoanMapper;
import com.company.model.*;
import com.company.repository.CustomerRepository;
import com.company.repository.LoanApplicationRepository;
import com.company.repository.LoanProductRepository;
import com.company.repository.RepaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanProductRepository loanProductRepository;
    private final CustomerRepository customerRepository;
    private final RepaymentRepository repaymentRepository;
    private final EmailService emailService;

    public LoanApplicationService(LoanApplicationRepository loanApplicationRepository,
                                  LoanProductRepository loanProductRepository,
                                  CustomerRepository customerRepository,
                                  RepaymentRepository repaymentRepository,
                                  EmailService emailService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanProductRepository = loanProductRepository;
        this.customerRepository = customerRepository;
        this.repaymentRepository = repaymentRepository;
        this.emailService = emailService;
    }

    // 1. Apply for a loan
    @Transactional
    public LoanApplicationResponseDTO applyForLoan(Long customerId, Long productId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Business rule: amount cannot exceed maxAmount
        if (amount.compareTo(product.getMaxAmount()) > 0) {
            throw new RuntimeException("Requested amount exceeds maximum allowed for this product");
        }

        LoanApplication application = new LoanApplication();
        application.setCustomer(customer);
        application.setProduct(product);
        application.setAmount(amount);
        application.setStatus(LoanStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());

        LoanApplication saved = loanApplicationRepository.save(application);

        // Email: Loan Creation
        emailService.sendLoanCreationEmail(customer.getEmail(), saved);

        return LoanMapper.toLoanApplicationResponseDTO(saved);
    }

    // 2. Approve a loan (Admin)
    @Transactional
    public LoanApplicationResponseDTO approveLoan(Long applicationId, String adminUsername) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Loan is not in PENDING status");
        }

        application.setStatus(LoanStatus.APPROVED);
        application.setApprovedDate(LocalDate.now());
        application.setApprovedBy(adminUsername);

        // Generate Repayment Schedule
        generateRepaymentSchedule(application);

        LoanApplication saved = loanApplicationRepository.save(application);

        // Email: Loan Approval
        emailService.sendLoanApprovalEmail(application.getCustomer().getEmail(), saved);

        return LoanMapper.toLoanApplicationResponseDTO(saved);
    }

    // 3. Reject a loan (Admin)
    @Transactional
    public LoanApplicationResponseDTO rejectLoan(Long applicationId, String reason) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Only pending applications can be rejected");
        }

        application.setStatus(LoanStatus.REJECTED);
        application.setRejectionReason(reason);

        LoanApplication saved = loanApplicationRepository.save(application);

        // Email: Loan Rejection
        emailService.sendLoanRejectionEmail(application.getCustomer().getEmail(), saved, reason);

        return LoanMapper.toLoanApplicationResponseDTO(saved);
    }

    // 4. Disburse a loan (Admin)
    @Transactional
    public LoanApplicationResponseDTO disburseLoan(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != LoanStatus.APPROVED) {
            throw new RuntimeException("Only approved loans can be disbursed");
        }

        application.setStatus(LoanStatus.DISBURSED);
        application.setDisbursedDate(LocalDate.now());
        application.setRemainingBalance(application.getAmount()); // Set remaining balance = full amount

        LoanApplication saved = loanApplicationRepository.save(application);

        // Email: Disbursement
        emailService.sendLoanDisbursementEmail(application.getCustomer().getEmail(), saved);

        return LoanMapper.toLoanApplicationResponseDTO(saved);
    }

    // 5. Make a repayment
    @Transactional
    public Repayment makeRepayment(Long applicationId, BigDecimal amount) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != LoanStatus.DISBURSED && application.getStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Loan is not active/disbursed");
        }

        // Validate repayment amount does not exceed remaining balance
        if (amount.compareTo(application.getRemainingBalance()) > 0) {
            throw new RuntimeException("Repayment amount exceeds remaining balance");
        }

        Repayment repayment = new Repayment();
        repayment.setLoanApplication(application);
        repayment.setAmount(amount);
        repayment.setDueDate(LocalDate.now());
        repayment.setPaidDate(LocalDate.now());

        Repayment saved = repaymentRepository.save(repayment);

        // Update remaining balance
        BigDecimal newBalance = application.getRemainingBalance().subtract(amount);
        application.setRemainingBalance(newBalance);

        // If balance is zero or less, mark as COMPLETED
        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            application.setStatus(LoanStatus.COMPLETED);
        } else {
            application.setStatus(LoanStatus.ACTIVE);
        }

        loanApplicationRepository.save(application);

        // Email: Repayment Confirmation
        emailService.sendRepaymentConfirmationEmail(application.getCustomer().getEmail(), application, saved);

        return saved;
    }

    // 6. Get application by ID (returns DTO)
    public LoanApplicationResponseDTO getApplicationById(Long id) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return LoanMapper.toLoanApplicationResponseDTO(application);
    }

    // 7. Get all applications for a customer (with Pagination)
    public Page<LoanApplicationResponseDTO> getApplicationsByCustomer(Long customerId, Pageable pageable) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Page<LoanApplication> page = loanApplicationRepository.findByCustomer(customer, pageable);
        return page.map(LoanMapper::toLoanApplicationResponseDTO);
    }

    // 8. Get all applications (Admin) — with Pagination
    public Page<LoanApplicationResponseDTO> getAllApplications(Pageable pageable) {
        Page<LoanApplication> page = loanApplicationRepository.findAll(pageable);
        return page.map(LoanMapper::toLoanApplicationResponseDTO);
    }

    // Helper: Generate repayment schedule
    private void generateRepaymentSchedule(LoanApplication application) {
        int months = application.getProduct().getTermMonths();
        BigDecimal monthlyAmount = application.getAmount().divide(BigDecimal.valueOf(months), 2, java.math.RoundingMode.HALF_UP);
        LocalDate startDate = LocalDate.now();

        for (int i = 1; i <= months; i++) {
            Repayment repayment = new Repayment();
            repayment.setLoanApplication(application);
            repayment.setAmount(monthlyAmount);
            repayment.setDueDate(startDate.plusMonths(i));
            repayment.setPaidDate(null);
            repaymentRepository.save(repayment);
        }
    }
}