package com.company.controller;

import com.company.dto.CustomerRequest;
import com.company.dto.CustomerResponse;
import com.company.dto.PageResponse;
import com.company.dto.ErrorResponse;


import com.company.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


//importing logs

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





import java.util.List;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/customers")

// the main class
public class CustomerController {
    //Logger class

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);


    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //pagination
    @GetMapping
    public PageResponse<CustomerResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String sort) {

        return customerService.getAllCustomers(page, size, name, email, sort);
    }

    // searching customerwith Id
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Customer with ID " + id + " not found"));
        }
        return ResponseEntity.ok(customer);
    }

    // Creating a customer
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        // adding simple log
        log.info("Received request to create customer with email: {}", request.getEmail());

        CustomerResponse created = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //Updatting a customer.
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        CustomerResponse updated = customerService.updateCustomer(id, request);
        if (updated == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Customer with ID " + id + " not found"));
        }
        return ResponseEntity.ok(updated);
    }

    // Updating customer by PATCHING ONLY 1 entity or field  EDITED
    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        CustomerResponse updated = customerService.patchCustomer(id, updates);
        if (updated == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Customer with ID " + id + " not found"));
        }
        return ResponseEntity.ok(updated);
    }


    // n + 1 fix
    @GetMapping("/with-addresses")
    public List<CustomerResponse> getAllWithAddresses() {
        return customerService.getAllCustomersWithAddresses();
    }










    // Deleting a Customer
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Customer with ID " + id + " not found"));
        }
        return ResponseEntity.noContent().build();
    }

    // Handle validation of  errors manually
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }
}