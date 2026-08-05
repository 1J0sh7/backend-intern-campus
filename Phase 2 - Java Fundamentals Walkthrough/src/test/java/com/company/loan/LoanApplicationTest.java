package com.company.loan;

import com.company.customer.Customer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoanApplicationTest {

    @Test
    void testLoanApplicationCreation() {
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        LoanProduct product = new LoanProduct("Personal", 5.5, 12);
        LoanApplication application = new LoanApplication(customer, product, 5000.0);

        assertEquals(customer, application.getCustomer());
        assertEquals(product, application.getProduct());
        assertEquals(5000.0, application.getAmount());
        assertEquals(LoanStatus.PENDING, application.getStatus());
    }

    @Test
    void testLoanApplicationStatusUpdate() {
        Customer customer = new Customer("John Doe", "john@email.com", "1234567890");
        LoanProduct product = new LoanProduct("Personal", 5.5, 12);
        LoanApplication application = new LoanApplication(customer, product, 5000.0);

        application.setStatus(LoanStatus.APPROVED);
        assertEquals(LoanStatus.APPROVED, application.getStatus());

        application.setStatus(LoanStatus.ACTIVE);
        assertEquals(LoanStatus.ACTIVE, application.getStatus());
    }
}