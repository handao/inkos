package com.inkos.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${inkos.mail.from}")
    private String from;

    public void sendVerificationCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("InkOS 注册验证码");
            helper.setText("<h3>您的 InkOS 注册验证码</h3>"
                    + "<p style=\"font-size:24px;letter-spacing:4px;font-weight:bold;\">" + code + "</p>"
                    + "<p>验证码有效期 10 分钟，请勿泄露给他人。</p>", true);
            mailSender.send(message);
            log.info("Verification code sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}
