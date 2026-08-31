package com.company.service;

import com.company.model.*;
import com.company.repository.LoanApplicationRepository;
import com.company.repository.LoanProductRepository;
import com.company.repository.CustomerRepository;
import com.company.repository.RepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanProductRepository loanProductRepository;
    private final CustomerRepository customerRepository;
    private final RepaymentRepository repaymentRepository;
    private final EmailService emailService;  // Reuse your existing SendGrid service

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
    public LoanApplication applyForLoan(Long customerId, Long productId, Double amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Business rule: amount cannot exceed maxAmount
        if (amount > product.getMaxAmount()) {
            throw new RuntimeException("Requested amount exceeds maximum allowed for this product");
        }

        LoanApplication application = new LoanApplication();
        application.setCustomer(customer);
        application.setProduct(product);
        application.setAmount(amount);
        application.setStatus(LoanStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());

        LoanApplication saved = loanApplicationRepository.save(application);

        // 🟢 EMAIL: Loan Creation
        emailService.sendLoanCreationEmail(customer.getEmail(), saved);

        return saved;
    }

    // 2. Approve a loan (Admin)
    @Transactional
    public LoanApplication approveLoan(Long applicationId, String adminUsername) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Loan is not in PENDING status");
        }

        application.setStatus(LoanStatus.APPROVED);
        application.setApprovedDate(LocalDate.now());
        application.setApprovedBy(adminUsername);

        // 🔥 Generate Repayment Schedule
        generateRepaymentSchedule(application);

        LoanApplication saved = loanApplicationRepository.save(application);

        // 🟢 EMAIL: Loan Approval
        emailService.sendLoanApprovalEmail(application.getCustomer().getEmail(), saved);

        return saved;
    }

    // 3. Reject a loan (Admin)
    @Transactional
    public LoanApplication rejectLoan(Long applicationId, String reason) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Only pending applications can be rejected");
        }

        application.setStatus(LoanStatus.REJECTED);
        application.setRejectionReason(reason);

        LoanApplication saved = loanApplicationRepository.save(application);

        // 🟢 EMAIL: Loan Rejection
        emailService.sendLoanRejectionEmail(application.getCustomer().getEmail(), saved, reason);

        return saved;
    }

    // 4. Disburse a loan (Admin)
    @Transactional
    public LoanApplication disburseLoan(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != LoanStatus.APPROVED) {
            throw new RuntimeException("Only approved loans can be disbursed");
        }

        application.setStatus(LoanStatus.DISBURSED);
        application.setDisbursedDate(LocalDate.now());

        LoanApplication saved = loanApplicationRepository.save(application);

        // 🟢 EMAIL: Disbursement
        emailService.sendLoanDisbursementEmail(application.getCustomer().getEmail(), saved);

        return saved;
    }

    // 5. Make a repayment
    @Transactional
    public Repayment makeRepayment(Long applicationId, Double amount) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != LoanStatus.DISBURSED && application.getStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Loan is not active/disbursed");
        }

        Repayment repayment = new Repayment();
        repayment.setLoanApplication(application);
        repayment.setAmount(amount);
        repayment.setDueDate(LocalDate.now()); // In real-life, this comes from schedule
        repayment.setPaidDate(LocalDate.now());

        Repayment saved = repaymentRepository.save(repayment);

        // Check if all scheduled repayments are paid -> mark as COMPLETED
        List<Repayment> allRepayments = repaymentRepository.findByLoanApplication(application);
        long totalPaid = allRepayments.stream().filter(Repayment::isPaid).count();
        // For simplicity, let's assume termMonths = number of expected payments.
        // We could implement full loan balance logic, but for now, if they pay >= termMonths times, mark complete.
        // (You can expand this with a proper loan amortization).
        // Let's just mark as ACTIVE for now unless we add full amortization logic.
        application.setStatus(LoanStatus.ACTIVE);
        loanApplicationRepository.save(application);

        // 🟢 EMAIL: Repayment Received
        emailService.sendRepaymentConfirmationEmail(application.getCustomer().getEmail(), application, saved);

        return saved;
    }

    // Helper: Generate repayment schedule
    private void generateRepaymentSchedule(LoanApplication application) {
        // e.g., split amount over termMonths (simple equal installments)
        int months = application.getProduct().getTermMonths();
        double monthlyAmount = application.getAmount() / months;
        LocalDate startDate = LocalDate.now();

        for (int i = 1; i <= months; i++) {
            Repayment repayment = new Repayment();
            repayment.setLoanApplication(application);
            repayment.setAmount(monthlyAmount);
            repayment.setDueDate(startDate.plusMonths(i));
            repayment.setPaidDate(null); // not paid yet
            repaymentRepository.save(repayment);
        }
    }

    // 6. Get application by ID
    public LoanApplication getApplicationById(Long id) {
        return loanApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    // 7. Get all applications for a customer
    public List<LoanApplication> getApplicationsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return loanApplicationRepository.findByCustomer(customer);
    }

    // 8. Get all applications (Admin)
    public List<LoanApplication> getAllApplications() {
        return loanApplicationRepository.findAll();
    }
}