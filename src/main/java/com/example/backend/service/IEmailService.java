package com.example.backend.service;

import com.example.backend.model.Email.EmailNotificationRequest;

public interface IEmailService {

    public void sendEmail(EmailNotificationRequest emailRequest);
}