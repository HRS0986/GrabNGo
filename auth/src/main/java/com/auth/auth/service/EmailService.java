package com.auth.auth.service;

import com.auth.auth.constants.Messages;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${mail.sender.name}")
    private String senderName;

    @Value("${mail.sender.email}")
    private String senderEmail;

    public EmailService(JavaMailSender javaMailSender){
        mailSender = javaMailSender;
    }

    public void sendForgetPasswordEmail(String email, String code) throws MessagingException, IOException {
        String filepath = "templates/reset-password-template.html";
        String emailContent = loadEmailTemplate(code, filepath);
        sendEmail(email, Messages.FORGET_PASSWORD_EMAIL_SUBJECT, emailContent);
    }

    private String loadEmailTemplate(String code, String filepath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filepath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String template = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            template = template.replace("{{verification_code}}", code);
            return template;
        }
    }

    private void sendEmail(String email, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(senderEmail, senderName);
        helper.setTo(email);

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}
