// com/hexavolt/backend/service/MailService.java
package com.hexavolt.backend.service;

public interface MailService {
    void send(String to, String subject, String text);
}
