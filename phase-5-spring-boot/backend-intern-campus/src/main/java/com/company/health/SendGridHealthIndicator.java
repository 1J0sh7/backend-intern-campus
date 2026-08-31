//package com.company.health;
//
//import com.sendgrid.SendGrid;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.HealthIndicator;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SendGridHealthIndicator implements HealthIndicator {
//
//    private final SendGrid sendGrid;
//
//    public SendGridHealthIndicator(SendGrid sendGrid) {
//        this.sendGrid = sendGrid;
//    }
//
//    @Override
//    public Health health() {
//        try {
//            // Check if API key is configured (basic check)
//            if (sendGrid.getApiKey() == null || sendGrid.getApiKey().isBlank()) {
//                return Health.down()
//                        .withDetail("error", "SendGrid API key is missing or empty")
//                        .build();
//            }
//
//            // Optional: You could make a lightweight API call to verify the key works
//            // but a simple presence check is enough for most cases.
//
//            return Health.up()
//                    .withDetail("service", "SendGrid")
//                    .withDetail("status", "API key is configured")
//                    .build();
//
//        } catch (Exception e) {
//            return Health.down()
//                    .withDetail("error", e.getMessage())
//                    .build();
//        }
//    }
//}