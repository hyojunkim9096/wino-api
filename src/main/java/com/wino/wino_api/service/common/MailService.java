package com.wino.wino_api.service.common;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}") private String from;
    @Value("${app.mail.enabled:true}") private boolean enabled;
    @Value("${app.mail.brand:WINO}") private String brand;

    public void sendPasswordResetCode(String to, String userId, String code, int ttlMin) {
        if (!enabled) {
            log.warn("[MAIL-DISABLED] to={}, code={}", to, code);
            return;
        }
        try {
            MimeMessage mm = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(mm, "UTF-8");
            h.setFrom(from);
            h.setTo(to);
            h.setSubject("[" + brand + "] 비밀번호 재설정 인증코드");

            String html = """
                <div style="font-family:system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif;line-height:1.6">
                  <h2>[%s] 비밀번호 재설정</h2>
                  <p><b>%s</b> 님, 아래 6자리 코드를 %d분 내에 입력해 주세요.</p>
                  <div style="font-size:24px;font-weight:700;letter-spacing:4px;margin:16px 0">%s</div>
                  <p>본인이 요청한 게 아니라면 이 메일을 무시하세요.</p>
                </div>
                """.formatted(brand, userId, ttlMin, code);

            h.setText(html, true);
            mailSender.send(mm);
            log.info("[MAIL] reset-code sent to {}", to);
        } catch (MailException e) {
            log.error("[MAIL-FAIL] to={}, msg={}", to, e.getMessage(), e);
        } catch (Exception e) {
            log.error("[MAIL-ERROR] to={}, msg={}", to, e.getMessage(), e);
        }
    }
}
