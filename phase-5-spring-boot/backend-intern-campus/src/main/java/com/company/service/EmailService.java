package com.company.service;

import com.company.model.LoanApplication;
import com.company.model.Repayment;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final SendGrid sendGrid;
    private final String fromEmail;
    private final String fromName;

    // Template IDs (hardcoded)
    private static final String TEMPLATE_WELCOME = "d-75a2fd64eab94866bb76bc28f1872edd";
    private static final String TEMPLATE_LOAN_CREATED = "d-6baf9c2677b149448fda1e886c3baaa9";
    private static final String TEMPLATE_LOAN_APPROVED = "d-50801dfa2535498b849e8060672a14e7";
    private static final String TEMPLATE_LOAN_REJECTED = "d-bcfe21e718a842e3a477e85db090bd5b";
    private static final String TEMPLATE_LOAN_DISBURSED = "d-335e389cd2e44066a865cc364fad8670";
    private static final String TEMPLATE_REPAYMENT = "d-1c0d7df118924c2aa98744b99ad77468";

    public EmailService(SendGrid sendGrid,
                        @Value("${sendgrid.from-email}") String fromEmail,
                        @Value("${sendgrid.from-name}") String fromName) {
        this.sendGrid = sendGrid;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    // 1. Welcome Email
    public void sendWelcomeEmail(String to, String name) {
        log.info("Sending welcome email to: {}", to);
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(to));
        personalization.addDynamicTemplateData("name", name);
        sendDynamicEmail(TEMPLATE_WELCOME, personalization);
    }

    // 2. Loan Application Received
    public void sendLoanCreationEmail(String to, LoanApplication loanApplication) {
        log.info("Sending loan creation email to: {}", to);
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(to));
        personalization.addDynamicTemplateData("name", loanApplication.getCustomer().getName());
        personalization.addDynamicTemplateData("applicationId", loanApplication.getId().toString());
        personalization.addDynamicTemplateData("amount", loanApplication.getAmount().toString());
        personalization.addDynamicTemplateData("productName", loanApplication.getProduct().getName());
        sendDynamicEmail(TEMPLATE_LOAN_CREATED, personalization);
    }

    // 3. Loan Approved
    public void sendLoanApprovalEmail(String to, LoanApplication loanApplication) {
        log.info("Sending loan approval email to: {}", to);
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(to));
        personalization.addDynamicTemplateData("name", loanApplication.getCustomer().getName());
        personalization.addDynamicTemplateData("applicationId", loanApplication.getId().toString());
        personalization.addDynamicTemplateData("amount", loanApplication.getAmount().toString());
        personalization.addDynamicTemplateData("productName", loanApplication.getProduct().getName());
        personalization.addDynamicTemplateData("interestRate", loanApplication.getProduct().getInterestRate().toString());
        personalization.addDynamicTemplateData("termMonths", loanApplication.getProduct().getTermMonths().toString());
        sendDynamicEmail(TEMPLATE_LOAN_APPROVED, personalization);
    }

    // 4. Loan Rejected
    public void sendLoanRejectionEmail(String to, LoanApplication loanApplication, String reason) {
        log.info("Sending loan rejection email to: {}", to);
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(to));
        personalization.addDynamicTemplateData("name", loanApplication.getCustomer().getName());
        personalization.addDynamicTemplateData("applicationId", loanApplication.getId().toString());
        personalization.addDynamicTemplateData("amount", loanApplication.getAmount().toString());
        personalization.addDynamicTemplateData("reason", reason != null ? reason : "No reason provided");
        sendDynamicEmail(TEMPLATE_LOAN_REJECTED, personalization);
    }

    // 5. Loan Disbursed
    public void sendLoanDisbursementEmail(String to, LoanApplication loanApplication) {
        log.info("Sending loan disbursement email to: {}", to);
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(to));
        personalization.addDynamicTemplateData("name", loanApplication.getCustomer().getName());
        personalization.addDynamicTemplateData("applicationId", loanApplication.getId().toString());
        personalization.addDynamicTemplateData("amount", loanApplication.getAmount().toString());
        personalization.addDynamicTemplateData("remainingBalance", loanApplication.getRemainingBalance().toString());
        personalization.addDynamicTemplateData("termMonths", loanApplication.getProduct().getTermMonths().toString());
        sendDynamicEmail(TEMPLATE_LOAN_DISBURSED, personalization);
    }

    // 6. Repayment Confirmation
    public void sendRepaymentConfirmationEmail(String to, LoanApplication loanApplication, Repayment repayment) {
        log.info("Sending repayment confirmation email to: {}", to);
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(to));
        personalization.addDynamicTemplateData("name", loanApplication.getCustomer().getName());
        personalization.addDynamicTemplateData("applicationId", loanApplication.getId().toString());
        personalization.addDynamicTemplateData("paymentAmount", repayment.getAmount().toString());
        personalization.addDynamicTemplateData("remainingBalance", loanApplication.getRemainingBalance().toString());
        personalization.addDynamicTemplateData("paymentDate", repayment.getPaidDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        sendDynamicEmail(TEMPLATE_REPAYMENT, personalization);
    }

    @Retryable(
            value = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    private void sendDynamicEmail(String templateId, Personalization personalization) {
        Mail mail = new Mail();
        mail.setFrom(new Email(fromEmail, fromName));
        mail.setTemplateId(templateId);
        mail.addPersonalization(personalization);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            log.info("Email sent. Status code: {}", response.getStatusCode());
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email sent successfully");
            } else {
                log.error("SendGrid returned error: {}", response.getBody());
                throw new RuntimeException("SendGrid returned error: " + response.getStatusCode());
            }
        } catch (IOException ex) {
            log.error("Failed to send email: {}", ex.getMessage());
            throw new RuntimeException("Failed to send email", ex);
        }
    }
}