package org.example.util;

import jakarta.mail.internet.MimeMessage;
import org.example.model.User;
import org.example.model.UserProduct;
import org.example.service.ThymeleafService;
import org.example.service.ThymeleafServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailUtil {
    private JavaMailSender emailSender;
    private ThymeleafServiceImpl thymeleafService;

    @Autowired
    public EmailUtil(JavaMailSender emailSender, ThymeleafServiceImpl thymeleafService) {
        this.emailSender = emailSender;
        this.thymeleafService = thymeleafService;
    }

    public void sendEmailSuccess(UserProduct userProduct) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            User user = userProduct.getUser();

            helper.setTo(user.getEmail());
            helper.setSubject("You successful provided service \"" + userProduct.getProduct().getProductName() + "\"");

            Map<String, Object> variables = new HashMap<>();
            String fullname = user.getLastName() + " " + user.getFirstName() + " " + user.getPatronymic();
            variables.put("full_name", fullname);
            variables.put("username", user.getUsername());
            variables.put("product_name", userProduct.getProduct().getProductName());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            variables.put("date_created", sdf.format(Date.from(userProduct.getDateOfCreated().atZone(ZoneId.systemDefault()).toInstant())));
            helper.setText(thymeleafService.createContent("success.html", variables), true);
            emailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendEmailFailed(User user, String productName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(user.getEmail());
            helper.setSubject("You failed to provide service \"" + productName + "\"");

            Map<String, Object> variables = new HashMap<>();
            String text = user.getLastName() + " " + user.getFirstName() + " " + user.getPatronymic() +
                    ", unfortunalely, you provide this service too late.";
            variables.put("text", text);
            helper.setText(thymeleafService.createContent("failed.html", variables), true);

            emailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
