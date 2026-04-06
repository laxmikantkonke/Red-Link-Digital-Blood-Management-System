package com.bloodhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@redlink.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("=== OFFLINE EMAIL FALLBACK ===");
            System.err.println("To: " + to);
            System.err.println("Subject: " + subject);
            System.err.println("Body: " + body);
            System.err.println("===============================");
            System.err.println("Note: Email could not be sent (likely offline). Details printed above.");
        }
    }
}
