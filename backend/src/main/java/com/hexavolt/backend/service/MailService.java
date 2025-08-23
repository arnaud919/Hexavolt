// com/hexavolt/backend/service/MailService.java
package com.hexavolt.backend.service;

public interface MailService {
    void sendHtml(String to, String subject, String html);
}
