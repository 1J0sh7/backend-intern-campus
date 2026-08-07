package com.company.service;

import com.company.dto.CustomerRequest;
import com.company.dto.CustomerResponse;
import com.company.exception.DuplicateEmailException;
import com.company.model.Customer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CustomerService {

    private List<Customer> customers = new ArrayList<>();
    private AtomicLong idCounter = new AtomicLong(1);

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }

    private Customer toEntity(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        return customer;
    }

    public List<CustomerResponse> getAllCustomers() {
        return customers.stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public CustomerResponse getCustomerById(Long id) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) {
                return toResponse(c);
            }
        }
        return null;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        for (Customer c : customers) {
            if (c.getEmail().equals(request.getEmail())) {
                throw new DuplicateEmailException("Customer with email " + request.getEmail() + " already exists");
            }
        }

        Customer customer = toEntity(request);
        customer.setId(idCounter.getAndIncrement());
        customers.add(customer);
        return toResponse(customer);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) {
                c.setName(request.getName());
                c.setEmail(request.getEmail());
                c.setPhone(request.getPhone());
                return toResponse(c);
            }
        }
        return null;
    }

    public boolean deleteCustomer(Long id) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) {
                customers.remove(c);
                return true;
            }
        }
        return false;
    }
}