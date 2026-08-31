package com.company.mapper;

import com.company.dto.CustomerResponse;
import com.company.dto.LoanApplicationResponseDTO;
import com.company.dto.LoanProductResponseDTO;
import com.company.dto.RepaymentResponseDTO;
import com.company.model.Customer;
import com.company.model.LoanApplication;
import com.company.model.LoanProduct;
import com.company.model.Repayment;

public class LoanMapper {

    // Convert Customer → CustomerResponse (no address for now)
    public static CustomerResponse toCustomerResponse(Customer customer) {
        if (customer == null) return null;
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }

    // Convert LoanProduct → LoanProductResponseDTO
    public static LoanProductResponseDTO toLoanProductResponseDTO(LoanProduct product) {
        if (product == null) return null;
        return new LoanProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getInterestRate(),
                product.getTermMonths(),
                product.getMaxAmount()
        );
    }

    // Convert LoanApplication → LoanApplicationResponseDTO
    public static LoanApplicationResponseDTO toLoanApplicationResponseDTO(LoanApplication application) {
        if (application == null) return null;
        return new LoanApplicationResponseDTO(
                application.getId(),
                toCustomerResponse(application.getCustomer()),
                toLoanProductResponseDTO(application.getProduct()),
                application.getAmount(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getApprovedDate(),
                application.getDisbursedDate(),
                application.getRemainingBalance()
        );
    }

    // Convert Repayment → RepaymentResponseDTO
    public static RepaymentResponseDTO toRepaymentResponseDTO(Repayment repayment) {
        if (repayment == null) return null;
        return new RepaymentResponseDTO(
                repayment.getId(),
                repayment.getAmount(),
                repayment.getDueDate(),
                repayment.getPaidDate(),
                repayment.isPaid()   // assumes you have isPaid() method
        );
    }
}