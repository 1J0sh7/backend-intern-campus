package com.company.service;

import com.company.dto.LoanApplicationResponseDTO;
import com.company.dto.RepaymentResponseDTO;
import com.company.exception.CustomerHasActiveLoanException;
import com.company.exception.InsufficientBalanceException;
import com.company.exception.LoanNotApprovedException;
import com.company.exception.LoanNotPendingException;
import com.company.exception.ResourceNotFoundException;
import com.company.exception.ValidationException;
import com.company.mapper.LoanMapper;
import com.company.model.*;
import com.company.repository.CustomerRepository;
import com.company.repository.LoanApplicationRepository;
import com.company.repository.LoanProductRepository;
import com.company.repository.RepaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    // ============================================================
    // Helper: Get current username from SecurityContext
    // ============================================================
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    // ============================================================
    // Helper: Check and update overdue status
    // ============================================================
    private void checkAndUpdateOverdueStatus(LoanApplication application) {
        // Only check ACTIVE or DISBURSED loans
        if (application.getStatus() != LoanStatus.ACTIVE &&
                application.getStatus() != LoanStatus.DISBURSED) {
            return;
        }

        if (application.getDisbursedDate() == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = application.getDisbursedDate()
                .plusMonths(application.getProduct().getTermMonths());

        if (today.isAfter(dueDate) &&
                application.getRemainingBalance().compareTo(BigDecimal.ZERO) > 0) {
            application.setStatus(LoanStatus.OVERDUE);
            loanApplicationRepository.save(application);
        }
    }

    // ============================================================
    // 1. Apply for a loan
    // ============================================================
    @Transactional
    public LoanApplicationResponseDTO applyForLoan(Long customerId, Long productId, BigDecimal amount) {
        String username = getCurrentUsername();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // CHECK OWNERSHIP
        if (customer.getUser() == null || !customer.getUser().getUsername().equals(username)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        // CHECK FOR ACTIVE LOANS
        List<LoanStatus> activeStatuses = Arrays.asList(
                LoanStatus.PENDING,
                LoanStatus.APPROVED,
                LoanStatus.DISBURSED,
                LoanStatus.ACTIVE,
                LoanStatus.OVERDUE
        );

        boolean hasActiveLoan = loanApplicationRepository.existsByCustomerAndStatusIn(customer, activeStatuses);

        if (hasActiveLoan) {
            throw new CustomerHasActiveLoanException(
                    "Customer already has an active loan (PENDING, APPROVED, DISBURSED, or ACTIVE). Cannot apply for a new loan."
            );
        }

        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Business rule: amount cannot exceed maxAmount
        if (amount.compareTo(product.getMaxAmount()) > 0) {
            throw new ValidationException("Requested amount exceeds maximum allowed for this product");
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

    // ============================================================
    // 2. Approve a loan (Admin)
    // ============================================================
    @Transactional
    public LoanApplicationResponseDTO approveLoan(Long applicationId, String adminUsername) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (application.getStatus() != LoanStatus.PENDING) {
            throw new LoanNotPendingException("Loan is not in PENDING status. Current status: " + application.getStatus());
        }

        application.setStatus(LoanStatus.APPROVED);
        application.setApprovedDate(LocalDate.now());
        application.setApprovedBy(adminUsername);

        LoanApplication saved = loanApplicationRepository.save(application);

        // Email: Loan Approval
        emailService.sendLoanApprovalEmail(application.getCustomer().getEmail(), saved);

        return LoanMapper.toLoanApplicationResponseDTO(saved);
    }

    // ============================================================
    // 3. Reject a loan (Admin)
    // ============================================================
    @Transactional
    public LoanApplicationResponseDTO rejectLoan(Long applicationId, String reason) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (application.getStatus() != LoanStatus.PENDING) {
            throw new LoanNotPendingException("Only pending applications can be rejected. Current status: " + application.getStatus());
        }

        application.setStatus(LoanStatus.REJECTED);
        application.setRejectionReason(reason);

        LoanApplication saved = loanApplicationRepository.save(application);

        // Email: Loan Rejection
        emailService.sendLoanRejectionEmail(application.getCustomer().getEmail(), saved, reason);

        return LoanMapper.toLoanApplicationResponseDTO(saved);
    }

    // ============================================================
    // 4. Disburse a loan (Admin) — WITH INTEREST CALCULATION
    // ============================================================
    @Transactional
    public LoanApplicationResponseDTO disburseLoan(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (application.getStatus() != LoanStatus.APPROVED) {
            throw new LoanNotApprovedException("Only approved loans can be disbursed. Current status: " + application.getStatus());
        }

        // Calculate interest
        BigDecimal principal = application.getAmount();
        BigDecimal interestRate = BigDecimal.valueOf(application.getProduct().getInterestRate());
        BigDecimal interestMultiplier = BigDecimal.ONE.add(interestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
        BigDecimal totalPayable = principal.multiply(interestMultiplier);

        application.setStatus(LoanStatus.DISBURSED);
        application.setDisbursedDate(LocalDate.now());
        application.setRemainingBalance(totalPayable);

        LoanApplication saved = loanApplicationRepository.save(application);

        // Email: Disbursement
        emailService.sendLoanDisbursementEmail(application.getCustomer().getEmail(), saved);

        return LoanMapper.toLoanApplicationResponseDTO(saved);
    }

    // 5. Make a repayment
    @Transactional
    public RepaymentResponseDTO makeRepayment(Long applicationId, BigDecimal amount) {
        String username = getCurrentUsername();

        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        // CHECK OWNERSHIP
        if (application.getCustomer() == null || application.getCustomer().getUser() == null) {
            throw new ResourceNotFoundException("Application not found with id: " + applicationId);
        }

        if (!application.getCustomer().getUser().getUsername().equals(username)) {
            throw new ResourceNotFoundException("Application not found with id: " + applicationId);
        }

        // CHECK OVERDUE STATUS
        checkAndUpdateOverdueStatus(application);

        if (application.getStatus() == LoanStatus.OVERDUE) {
            throw new ValidationException("This loan is OVERDUE. Please contact support to arrange payment.");
        }

        if (application.getStatus() == LoanStatus.COMPLETED) {
            throw new ValidationException("This loan has already been fully paid.");
        }

        if (application.getStatus() != LoanStatus.DISBURSED && application.getStatus() != LoanStatus.ACTIVE) {
            throw new ValidationException("Loan is not active/disbursed. Current status: " + application.getStatus());
        }

        // Validate amount is not negative
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount cannot be negative or zero");
        }

        //  Validate amount does not exceed remaining balance
        if (amount.compareTo(application.getRemainingBalance()) > 0) {
            throw new InsufficientBalanceException("Repayment amount exceeds remaining balance");
        }

        //  DYNAMIC MONTHLY MINIMUM
        int termMonths = application.getProduct().getTermMonths();
        LocalDate disbursedDate = application.getDisbursedDate();
        LocalDate now = LocalDate.now();

        // Count how many months have passed since disbursement
        long monthsPassed = java.time.temporal.ChronoUnit.MONTHS.between(disbursedDate, now);
        int remainingMonths = (int) Math.max(1, termMonths - monthsPassed);

        // Minimum = remaining balance / remaining months
        BigDecimal monthlyMinimum = application.getRemainingBalance()
                .divide(BigDecimal.valueOf(remainingMonths), 2, RoundingMode.HALF_UP);

        if (amount.compareTo(monthlyMinimum) < 0) {
            throw new ValidationException(
                    "Amount is less than the minimum monthly payment of $" + monthlyMinimum.toPlainString()
            );
        }

        // Create repayment record
        Repayment repayment = new Repayment();
        repayment.setLoanApplication(application);
        repayment.setAmount(amount);
        repayment.setDueDate(LocalDate.now());
        repayment.setPaidDate(LocalDate.now());
        repayment.setStatus("PAID");

        Repayment saved = repaymentRepository.save(repayment);

        // Update remaining balance
        BigDecimal newBalance = application.getRemainingBalance().subtract(amount);
        application.setRemainingBalance(newBalance);

        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            application.setStatus(LoanStatus.COMPLETED);
        } else {
            application.setStatus(LoanStatus.ACTIVE);
        }

        loanApplicationRepository.save(application);

        // Email: Repayment Confirmation
        emailService.sendRepaymentConfirmationEmail(application.getCustomer().getEmail(), application, saved);

        return LoanMapper.toRepaymentResponseDTO(saved, application.getRemainingBalance());
    }
    // ============================================================
    // 6. Get application by ID
    // ============================================================
    public LoanApplicationResponseDTO getApplicationById(Long id) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        checkAndUpdateOverdueStatus(application);

        return LoanMapper.toLoanApplicationResponseDTO(application);
    }

    // ============================================================
    // 7. Get application by ID for user (with ownership check)
    // ============================================================
    public LoanApplicationResponseDTO getApplicationByIdForUser(Long id, String username) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with id: " + id));

        if (application.getCustomer() == null || application.getCustomer().getUser() == null) {
            throw new ResourceNotFoundException("Loan application not found with id: " + id);
        }

        String loanOwnerUsername = application.getCustomer().getUser().getUsername();
        if (!loanOwnerUsername.equals(username)) {
            throw new ResourceNotFoundException("Loan application not found with id: " + id);
        }

        checkAndUpdateOverdueStatus(application);

        return LoanMapper.toLoanApplicationResponseDTO(application);
    }

    // ============================================================
    // 8. Get all applications for a customer (with Pagination)
    // ============================================================
    public Page<LoanApplicationResponseDTO> getApplicationsByCustomer(Long customerId, Pageable pageable, String username) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        if (customer.getUser() == null || !customer.getUser().getUsername().equals(username)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        Page<LoanApplication> page = loanApplicationRepository.findByCustomer(customer, pageable);
        page.getContent().forEach(this::checkAndUpdateOverdueStatus);

        return page.map(LoanMapper::toLoanApplicationResponseDTO);
    }

    // ============================================================
    // 9. Get all applications for a customer (Admin)
    // ============================================================
    public Page<LoanApplicationResponseDTO> getApplicationsByCustomerForAdmin(Long customerId, Pageable pageable) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Page<LoanApplication> page = loanApplicationRepository.findByCustomer(customer, pageable);
        page.getContent().forEach(this::checkAndUpdateOverdueStatus);

        return page.map(LoanMapper::toLoanApplicationResponseDTO);
    }

    // ============================================================
    // 10. Get all applications (Admin)
    // ============================================================
    public Page<LoanApplicationResponseDTO> getAllApplications(Pageable pageable) {
        Page<LoanApplication> page = loanApplicationRepository.findAll(pageable);
        page.getContent().forEach(this::checkAndUpdateOverdueStatus);

        return page.map(LoanMapper::toLoanApplicationResponseDTO);
    }
}