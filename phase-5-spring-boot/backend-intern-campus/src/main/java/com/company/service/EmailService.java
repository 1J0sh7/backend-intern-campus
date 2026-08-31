package com.company.service;

import com.company.model.LoanApplication;
import com.company.model.Repayment;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final SendGrid sendGrid;
    private final String fromEmail;
    private final String fromName;

    public EmailService(SendGrid sendGrid,
                        @Value("${sendgrid.from-email}") String fromEmail,
                        @Value("${sendgrid.from-name}") String fromName) {
        this.sendGrid = sendGrid;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    public void sendWelcomeEmail(String to, String name) {
        log.info("Attempting to send welcome email to: {}", to);

        Email from = new Email(fromEmail, fromName);
        Email toEmail = new Email(to);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId("d-75a2fd64eab94866bb76bc28f1872edd");

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);
        personalization.addDynamicTemplateData("name", name);
        mail.addPersonalization(personalization);

        send(mail);
    }

    public void sendLoanCreationEmail(String to, LoanApplication loanApplication) {
        String subject = "Loan application received";
        String body = "Your loan application for " + loanApplication.getAmount() + " has been received and is pending review.";
        sendPlainTextEmail(to, subject, body);
    }

    public void sendLoanApprovalEmail(String to, LoanApplication loanApplication) {
        String subject = "Loan approved";
        String body = "Your loan application for " + loanApplication.getAmount() + " has been approved.";
        sendPlainTextEmail(to, subject, body);
    }

    public void sendLoanRejectionEmail(String to, LoanApplication loanApplication, String reason) {
        String subject = "Loan application update";
        String body = "Your loan application for " + loanApplication.getAmount() + " was rejected. Reason: " + reason;
        sendPlainTextEmail(to, subject, body);
    }

    public void sendLoanDisbursementEmail(String to, LoanApplication loanApplication) {
        String subject = "Loan disbursed";
        String body = "Your loan of " + loanApplication.getAmount() + " has been disbursed.";
        sendPlainTextEmail(to, subject, body);
    }

    public void sendRepaymentConfirmationEmail(String to, LoanApplication loanApplication, Repayment repayment) {
        String subject = "Repayment received";
        String body = "Payment of " + repayment.getAmount() + " was received for your loan application of " + loanApplication.getAmount() + ".";
        sendPlainTextEmail(to, subject, body);
    }

    private void sendPlainTextEmail(String to, String subject, String body) {
        Email from = new Email(fromEmail, fromName);
        Email recipient = new Email(to);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addContent(new Content("text/plain", body));

        Personalization personalization = new Personalization();
        personalization.addTo(recipient);
        mail.addPersonalization(personalization);

        send(mail);
    }

    @Retryable(
            value = {RuntimeException.class},  // <-- Retry on RuntimeException (which wraps IOException)
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    private void send(Mail mail) {
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
                // Throw unchecked exception so @Retryable can retry
                throw new RuntimeException("SendGrid returned error status: " + response.getStatusCode());
            }
        } catch (IOException ex) {
            log.error("Failed to send email: {}", ex.getMessage());
            // Wrap checked IOException in unchecked RuntimeException for @Retryable
            throw new RuntimeException("Failed to send email", ex);
        }
    }
}