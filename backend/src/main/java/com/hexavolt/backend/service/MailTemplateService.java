package com.hexavolt.backend.service;

import com.hexavolt.backend.entity.User;

public interface MailTemplateService {
    String activationEmail(User user, String activationLink);
    String resetPasswordEmail(User user, String resetLink);
}
