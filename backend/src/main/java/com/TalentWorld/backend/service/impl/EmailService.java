package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.excepiton.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public String sendMail(String to, String subject, String content) {
        try {

            log.info("Sending email to {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);

            return "success";
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new BusinessException(e.getMessage(), "Error_While_Sending_Email", HttpStatus.BAD_REQUEST);
        }
    }

}
