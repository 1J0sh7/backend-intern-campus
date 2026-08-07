package com.company.service;

import com.company.dto.CustomerRequest;
import com.company.dto.CustomerResponse;
import com.company.model.Customer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final List<Customer> customers = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

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
                .collect(Collectors.toList());
    }

    public CustomerResponse getCustomerById(Long id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(this::toResponse)
                .orElse(null);
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        // Check for duplicate email
        for (Customer c : customers) {
            if (c.getEmail().equalsIgnoreCase(request.getEmail())) {
                return null;  // Indicates duplicate
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
                // Check duplicate email only if changed
                if (!c.getEmail().equalsIgnoreCase(request.getEmail())) {
                    for (Customer other : customers) {
                        if (other.getId().equals(id)) continue;
                        if (other.getEmail().equalsIgnoreCase(request.getEmail())) {
                            return null; // duplicate email in update
                        }
                    }
                }
                c.setName(request.getName());
                c.setEmail(request.getEmail());
                c.setPhone(request.getPhone());
                return toResponse(c);
            }
        }
        return null; // not found
    }

    public boolean deleteCustomer(Long id) {
        return customers.removeIf(c -> c.getId().equals(id));
    }
}