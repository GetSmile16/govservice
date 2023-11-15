package org.example.util;

import jakarta.mail.internet.MimeMessage;
import org.example.model.EmailRetry;
import org.example.model.User;
import org.example.model.UserProduct;
import org.example.repository.EmailRetryRepository;
import org.example.service.ThymeleafServiceImpl;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmailUtil {
    private final JavaMailSender emailSender;
    private final ThymeleafServiceImpl thymeleafService;
    private final EmailRetryRepository emailRetryRepository;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public EmailUtil(JavaMailSender emailSender,
                     ThymeleafServiceImpl thymeleafService,
                     EmailRetryRepository emailRetryRepository) {
        this.emailSender = emailSender;
        this.thymeleafService = thymeleafService;
        this.emailRetryRepository = emailRetryRepository;
    }

    public void sendEmailSuccess(UserProduct userProduct) {
        Map<String, Object> variables = new HashMap<>();
        User user = userProduct.getUser();
        String fullname = user.getLastName() + " " + user.getFirstName() + " " + user.getPatronymic();
        variables.put("full_name", fullname);
        variables.put("username", user.getUsername());
        variables.put("product_name", userProduct.getProduct().getProductName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        variables.put("date_created", sdf.format
                (
                        Date.from(userProduct.getDateOfCreated().atZone(ZoneId.systemDefault()).toInstant())
                ));
        String content = thymeleafService.createContent("success.html", variables);
        String addressTo = user.getEmail();
        String subject = "You successful provided service \"" + userProduct.getProduct().getProductName() + "\"";
        sendEmail(content, addressTo, subject);
    }

    public void sendEmailFailed(User user, String productName) {
        Map<String, Object> variables = new HashMap<>();
        String fullname = user.getLastName() + " " + user.getFirstName() + " " + user.getPatronymic();
        String text = fullname + ", unfortunalely, you provide this service too late.";
        variables.put("text", text);
        String content = thymeleafService.createContent("failed.html", variables);
        String addressTo = user.getEmail();
        String subject = "You failed to provide service \"" + productName + "\"";
        sendEmail(content, addressTo, subject);
    }

    private void sendEmail(String content, String addressTo, String subject) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(addressTo);
            helper.setSubject(subject);
            helper.setText(content, true);

            emailSender.send(message);
            log.info("Email send to " + addressTo);
        } catch (Exception e) {
            log.warn("Failed to send email to " + addressTo);
            emailRetryRepository.save(
                    new EmailRetry(
                            addressTo,
                            subject,
                            content
                    )
            );
        }
    }

    private void sendEmail(EmailRetry emailRetry) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(emailRetry.getAddressTo());
            helper.setSubject(emailRetry.getSubject());
            helper.setText(emailRetry.getContent(), true);

            emailSender.send(message);
            emailRetryRepository.deleteById(emailRetry.getId());
            log.info("Email send to " + emailRetry.getAddressTo());
        } catch (Exception e) {
            log.warn("Retry failed to send email to " + emailRetry.getAddressTo());
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void retryEmails() {
        List<EmailRetry> failedEmails = emailRetryRepository.findAllByOrderByRetryTimeAsc();
        for (EmailRetry emailRetry : failedEmails) {
            sendEmail(emailRetry);
        }
    }
}
