package com.mockproject.group3.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(javaMailSender);
    }

    @Test
    void testSendEmail() throws MessagingException, IOException {
        // Create a mock MimeMessage
        MimeMessage mimeMessage = new MimeMessage((jakarta.mail.Session) null);

        // Ensure that when createMimeMessage is called, the mock MimeMessage is returned
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Mock the readTemplate method to return a template string
        EmailService emailServiceSpy = org.mockito.Mockito.spy(emailService);
        when(emailServiceSpy.readTemplate("templates/test-template.html")).thenReturn("<html><body>Hello {email},</body></html>");

        // Call the method to be tested
        emailServiceSpy.sendEmail("test@example.com", "Test Subject", "templates/test-template");

        // Verify that the send method was called correctly
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendEmailVerify() throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = new MimeMessage((jakarta.mail.Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmailVerify("test@example.com", "Test Subject", "Test Body");

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendPayoutEmail() throws IOException, MessagingException {
        MimeMessage mimeMessage = new MimeMessage((jakarta.mail.Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailService emailServiceSpy = org.mockito.Mockito.spy(emailService);
        when(emailServiceSpy.readTemplate("templates/test-template.html")).thenReturn("<html><body>Hello {email}, you have received {amount}.</body></html>");

        emailServiceSpy.sendPayoutEmail("test@example.com", "Payout Notification", 100.00, "templates/test-template");

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testGetLinkResetPassword() {
        // Create a mock HttpServletRequest
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);

        // Mock the behavior of getRequestURL and getServletPath
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/app/reset-password"));
        when(request.getServletPath()).thenReturn("/reset-password");

        // Call the method to be tested
        String resetPasswordLink = EmailService.getLinkResetPassword(request);

        // Verify the expected result
        assertEquals("http://example.com/app", resetPasswordLink);
    }

    
}
