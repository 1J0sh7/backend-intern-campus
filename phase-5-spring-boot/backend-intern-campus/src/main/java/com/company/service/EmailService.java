package com.company.service;

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
        mail.setTemplateId("d-75a2fd64eab94866bb76bc28f1872edd"); // my template id of welcome 

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);
        personalization.addDynamicTemplateData("name", name);
        mail.addPersonalization(personalization);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            log.info("Email sent. Status code: {}", response.getStatusCode());
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Welcome email sent successfully to: {}", to);
            } else {
                log.error("SendGrid returned error: {}", response.getBody());
            }
        } catch (IOException ex) {
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }
}