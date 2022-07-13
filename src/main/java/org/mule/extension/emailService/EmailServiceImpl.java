package org.mule.extension.emailService;

import org.mule.extension.configuration.EmailFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender emailSender;

    public EmailServiceImpl(String host, Integer port, String username, String password)
    {
        emailSender = EmailFactory.createEmailConfiguration(host, port, username, password);
    }
    @Override
    public void sendEmail(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        emailSender.send(message);

        LOGGER.info("Uda?o sie wys?ac meila");
    }
}
