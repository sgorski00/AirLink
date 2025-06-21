package pl.sgorski.AirLink.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.configuration.MailProperties;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    /**
     * Sends an email with the specified parameters.
     *
     * @param to      the recipient's email address
     * @param subject the subject of the email
     * @param body    the body of the email, can be HTML formatted
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, mailProperties.getDefaultEncoding());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(mailProperties.getUsername());
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to create email message", e);
        }
    }
}
