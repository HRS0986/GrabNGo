package com.auth.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(emailService, "senderEmail", "test@example.com");
        ReflectionTestUtils.setField(emailService, "senderName", "Test Sender");
    }

    @Test
    void testSendForgetPasswordEmail() throws MessagingException, IOException {
        String email = "user@example.com";
        String code = "123456";
        String actionURL = "http://example.com/reset-password";

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        emailService.sendForgetPasswordEmail(email, code);

        verify(mailSender, times(1)).send(mockMessage);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        MimeMessage capturedMessage = messageCaptor.getValue();

        assertTrue(capturedMessage != null);
    }
}
