package com.company.service;

import com.company.dto.CustomerRequest;
import com.company.dto.CustomerResponse;
import com.company.dto.PageResponse;
import com.company.exception.DuplicateEmailException;
import com.company.model.Customer;
import org.springframework.stereotype.Service;
import com.company.exception.ValidationException;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.Map;



@Service
public class CustomerService {

    private final List<Customer> customers = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // MAPPERS

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

    // DUPLICATE CHECKS


//email

    private void checkDuplicateEmail(String email) {
        for (Customer c : customers) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                throw new DuplicateEmailException("Customer with that email " + email + " already exists");
            }
        }
    }



//phone number


    private void checkDuplicatePhone(String phone) {
        for (Customer c : customers) {
            if (c.getPhone().equals(phone)) {
                throw new DuplicateEmailException("Customer with that phone number " + phone + " already exists");
            }
        }
    }



// duplicate for update email


    private void checkDuplicateEmailForUpdate(Long id, String email) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) continue;
            if (c.getEmail().equalsIgnoreCase(email)) {
                throw new DuplicateEmailException("You cannot update with that email because it already exists");
            }
        }
    }


//duplicate for update phone number


    private void checkDuplicatePhoneForUpdate(Long id, String phone) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) continue;
            if (c.getPhone().equals(phone)) {
                throw new DuplicateEmailException("You cannot update with that phone number because it already exists");
            }
        }
    }


    // CRUD METHODS
//getting all the customers

    public List<CustomerResponse> getAllCustomers() {
        return customers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


//searching using id of customer


    public CustomerResponse getCustomerById(Long id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(this::toResponse)
                .orElse(null);
    }


// creating our customer

    public CustomerResponse createCustomer(CustomerRequest request) {
        checkDuplicateEmail(request.getEmail());
        checkDuplicatePhone(request.getPhone());

        Customer customer = toEntity(request);
        customer.setId(idCounter.getAndIncrement());
        customers.add(customer);
        return toResponse(customer);
    }


// updating the customer

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) {

                // Only checking contents if the field has changed

                if (!c.getEmail().equalsIgnoreCase(request.getEmail())) {
                    checkDuplicateEmailForUpdate(id, request.getEmail());
                }
                if (!c.getPhone().equals(request.getPhone())) {
                    checkDuplicatePhoneForUpdate(id, request.getPhone());
                }

                c.setName(request.getName());
                c.setEmail(request.getEmail());
                c.setPhone(request.getPhone());
                return toResponse(c);
            }
        }
        return null; // not found (404)
    }

    public boolean deleteCustomer(Long id) {
        return customers.removeIf(c -> c.getId().equals(id));
    }



// Patching updating customer with only one field changed

    public CustomerResponse patchCustomer(Long id, Map<String, Object> updates) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) {
                // Check if phone is being updated
                if (updates.containsKey("phone")) {
                    String newPhone = (String) updates.get("phone");
                    if (newPhone == null || newPhone.isEmpty()) {
                        throw new ValidationException("Phone cannot be null or empty");
                    }
                    if (!c.getPhone().equals(newPhone)) {
                        checkDuplicatePhoneForUpdate(id, newPhone);
                    }
                    c.setPhone(newPhone);
                }

                // Check if email is being updated
                if (updates.containsKey("email")) {
                    String newEmail = (String) updates.get("email");
                    if (newEmail == null || newEmail.isEmpty()) {
                        throw new ValidationException("Email cannot be null or empty");
                    }
                    // Email format validation
                    if (!isValidEmail(newEmail)) {
                        throw new ValidationException("Email must be valid");
                    }
                    if (!c.getEmail().equalsIgnoreCase(newEmail)) {
                        checkDuplicateEmailForUpdate(id, newEmail);
                    }
                    c.setEmail(newEmail);
                }

                // Check if name is being updated
                if (updates.containsKey("name")) {
                    String newName = (String) updates.get("name");
                    if (newName == null || newName.isEmpty()) {
                        throw new ValidationException("Name cannot be null or empty");
                    }
                    c.setName(newName);
                }

                return toResponse(c);
            }
        }
        return null;
    }

    // PAGINATION

    public PageResponse<CustomerResponse> getAllCustomers(
            int page,
            int size,
            String name,
            String email,
            String sort) {

        List<Customer> filtered = new ArrayList<>(customers);

        // Filtering  by name
        if (name != null && !name.isEmpty()) {
            filtered = filtered.stream()
                    .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filter by email
        if (email != null && !email.isEmpty()) {
            filtered = filtered.stream()
                    .filter(c -> c.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Sorting
        if (sort != null && !sort.isEmpty()) {
            filtered.sort(getComparator(sort));
        }

        // Paginate
        long totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());

        List<Customer> paginated = filtered.subList(start, end);

        List<CustomerResponse> responseContent = paginated.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(responseContent, page, size, totalElements);
    }

    //  HELPERS
    // email validation
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    // FOR SORTING

    private Comparator<Customer> getComparator(String sort) {
        String[] sortParts = sort.split(",");
        String field = sortParts[0];
        String direction = sortParts.length > 1 ? sortParts[1] : "asc";

        Comparator<Customer> comparator;
        switch (field) {
            case "name":
                comparator = Comparator.comparing(Customer::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "email":
                comparator = Comparator.comparing(Customer::getEmail, String.CASE_INSENSITIVE_ORDER);
                break;
            case "id":
            default:
                comparator = Comparator.comparing(Customer::getId);
                break;
        }

        if (direction.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}