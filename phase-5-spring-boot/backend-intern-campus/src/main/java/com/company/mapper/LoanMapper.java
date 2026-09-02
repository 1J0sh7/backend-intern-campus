package com.company.mapper;

import com.company.dto.CustomerResponse;
import com.company.dto.LoanApplicationResponseDTO;
import com.company.dto.LoanProductResponseDTO;
import com.company.dto.RepaymentResponseDTO;
import com.company.model.Customer;
import com.company.model.LoanApplication;
import com.company.model.LoanProduct;
import com.company.model.Repayment;

import java.math.BigDecimal;

public class LoanMapper {

    public static CustomerResponse toCustomerResponse(Customer customer) {
        if (customer == null) return null;
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }

    public static LoanProductResponseDTO toLoanProductResponseDTO(LoanProduct product) {
        if (product == null) return null;
        return new LoanProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getInterestRate().doubleValue(),    // Convert BigDecimal → Double
                product.getTermMonths(),
                product.getMaxAmount().doubleValue()        // Convert BigDecimal → Double
        );
    }

    public static LoanApplicationResponseDTO toLoanApplicationResponseDTO(LoanApplication application) {
        if (application == null) return null;
        return new LoanApplicationResponseDTO(
                application.getId(),
                toCustomerResponse(application.getCustomer()),
                toLoanProductResponseDTO(application.getProduct()),
                application.getAmount().doubleValue(),           // Convert BigDecimal → Double
                application.getStatus(),
                application.getCreatedAt(),
                application.getApprovedDate(),
                application.getDisbursedDate(),
                application.getRemainingBalance() != null ? application.getRemainingBalance().doubleValue() : null
        );
    }
    public static RepaymentResponseDTO toRepaymentResponseDTO(Repayment repayment, BigDecimal remainingBalance) {
        if (repayment == null) return null;
        return new RepaymentResponseDTO(
                repayment.getId(),
                repayment.getAmount().doubleValue(),
                repayment.getDueDate(),
                repayment.getPaidDate(),
                repayment.isPaid(),
                remainingBalance != null ? remainingBalance.doubleValue() : 0.0
        );
    }


    }
