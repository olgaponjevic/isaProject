package com.example.isaBack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.activation.url}")
    private String activationUrl;

    @Async
    public void sendActivationEmail(String toEmail, String username, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Aktivacija naloga - Jutjubić");
        message.setText(
                "Zdravo " + username + ",\n\n" +
                        "Hvala što si se registrovala na Jutjubić!\n\n" +
                        "Klikni na sledeći link da aktiviraš svoj nalog:\n" +
                        activationUrl + "?token=" + token + "\n\n" +
                        "Link važi 24 sata.\n\n" +
                        "Pozdrav,\nJutjubić tim"
        );
        mailSender.send(message);
    }
}