package org.mule.extension.emailService;

public interface EmailService {
    void sendEmail(String from, String to, String subject, String text);
}
