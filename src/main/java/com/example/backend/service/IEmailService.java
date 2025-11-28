package com.example.backend.service;

import com.example.backend.model.Email.EmailNotificationRequest;

public interface IEmailService {
    public void simulateEmailSending(EmailNotificationRequest emailRequest);

    public void sendEmail(EmailNotificationRequest emailRequest);
}