package com.mockproject.group3.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class EmailService {
    private final static String EXTENSION_FILE = ".html";
    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String templateName) {
        try {
            String htmlTemplate = readTemplate(templateName + EXTENSION_FILE);
            String htmlContent = htmlTemplate.replace("{email}", to);
            initMail(to, subject, htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEmailVerify(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress("minhdangtank1@gmail.com", "no-reply.abc@cursus.com"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void sendPayoutEmail(String to, String subject, double amount, String templateName) {

        try {
            String htmlTemplate = readTemplate(templateName + EXTENSION_FILE);
            String htmlContent = htmlTemplate.replace("{email}", to).replace("{amount}", String.valueOf(amount));
            initMail(to, subject, htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMail(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress("minhdangtank1@gmail.com", "no-reply.abc@cursus.com"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected String readTemplate(String pathUrl) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resourceUrl = classLoader.getResource(pathUrl);

        if (resourceUrl == null) {
            throw new IOException("Resource not found: " + pathUrl);
        }
        try {
            URI uri = resourceUrl.toURI();
            Path path = Paths.get(uri);
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI syntax: " + resourceUrl, e);
        }
    }

    // public void sendVerificationEmail(String to, String verificationUrl) throws IOException {
    //     String template = readTemplateVerify("templates/verification-email-template.txt");
    //     String message = template.replace("{{verificationUrl}}", verificationUrl);
    //     sendEmail(to, "Please verify your registration", message);
    // }

    // protected String readTemplateVerify(String path) throws IOException {
    //     ClassPathResource resource = new ClassPathResource(path);
    //     try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
    //         return reader.lines().collect(Collectors.joining("\n"));
    //     }
    // }

    public static String getLinkResetPassword(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return url.replace(request.getServletPath(), "");
    }

}
