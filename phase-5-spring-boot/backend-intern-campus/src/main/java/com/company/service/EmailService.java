package com.company.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String fromEmail;

    public EmailService(RestClient.Builder restClientBuilder,
                        @Value("${email.mailgun.url}") String mailgunUrl,
                        @Value("${email.mailgun.api-key}") String apiKey,
                        @Value("${email.mailgun.from}") String fromEmail) {
        this.restClient = restClientBuilder
                .baseUrl(mailgunUrl)
                .build();
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
    }

    public void sendWelcomeEmail(String to, String name) {
        log.info("Sending welcome email to: {}", to);

        try {
            restClient.post()
                    .uri("/messages")
                    .headers(headers -> headers.setBasicAuth("api", apiKey))
                    .body(Map.of(
                            "from", fromEmail,
                            "to", to,
                            "subject", "Welcome to Our Loan Platform!",
                            "text", "Thank you for creating a customer profile on our loan platform. Please go ahead and choose the loan packages you want."
                    ))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("Mailgun client error: {}", res.getStatusCode());
                        throw new RuntimeException("Mailgun API client error: " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("Mailgun server error: {}", res.getStatusCode());
                        throw new RuntimeException("Mailgun API server error: " + res.getStatusCode());
                    })
                    .toBodilessEntity();

            log.info("Welcome email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            // Don't throw — just log and continue
        }
    }
}