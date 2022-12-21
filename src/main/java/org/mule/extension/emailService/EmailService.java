package org.mule.extension.emailService;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService {

    private final JavaMailSender emailSender;
    private final String from;
    private final String to;
    private final String subjectPrefix;
    private String subject;

    public EmailService(JavaMailSender emailSender, String from, String to, String subjectPrefix) {
        this.from = from;
        this.to = to;
        this.emailSender = emailSender;
        this.subjectPrefix = subjectPrefix;
    }

    public void sendEmail(String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        emailSender.send(message);
    }

    public void setSubject(String channel) {
        this.subject = subjectPrefix + " " + channel;
    }
}