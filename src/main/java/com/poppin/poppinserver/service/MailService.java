package com.poppin.poppinserver.service;

import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender emailSender;

    public void sendEmail(String to, String title, String authCode) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(title);

            // HTML 형식으로 메일 내용 설정
            String htmlContent = "<h3>" + title + "</h3>" +
                    "<p>Poppin 인증코드는 <strong>" + authCode + "</strong>입니다.</p>";
            helper.setText(htmlContent, true); // true로 설정, HTML을 사용 가능

            emailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (MessagingException e) {
            throw new CommonException(ErrorCode.MAIL_SEND_ERROR);
        }
    }
}
