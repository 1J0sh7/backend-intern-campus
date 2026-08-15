package com.company.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct product;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, ACTIVE, COMPLETED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL)
    private List<Repayment> repayments = new ArrayList<>();

    // Constructors
    public LoanApplication() {}

    public LoanApplication(Customer customer, LoanProduct product, Double amount, String status) {
        this.customer = customer;
        this.product = product;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public LoanProduct getProduct() { return product; }
    public void setProduct(LoanProduct product) { this.product = product; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Repayment> getRepayments() { return repayments; }
    public void setRepayments(List<Repayment> repayments) { this.repayments = repayments; }
}