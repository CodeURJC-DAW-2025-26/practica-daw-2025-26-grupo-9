package es.urjc.daw.equis.service;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.samskivert.mustache.Mustache;

import es.urjc.daw.equis.model.User;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Mustache.Compiler mustacheCompiler;

    public EmailService(JavaMailSender mailSender,
                        Mustache.Compiler mustacheCompiler) {
        this.mailSender = mailSender;
        this.mustacheCompiler = mustacheCompiler;
    }

    public void sendTemplateEmail(String to,
                                  String subject,
                                  String templateName,
                                  Map<String, Object> model) {

        try {

            // Cargar template desde resources/templates
            var template = mustacheCompiler.compile(
                    new String(
                        getClass()
                        .getResourceAsStream("/templates/" + templateName + ".html")
                        .readAllBytes(),
                        StandardCharsets.UTF_8
                    )
            );

            String htmlContent = template.execute(model);

            MimeMessage message = mailSender.createMimeMessage();
            message.setHeader("Content-Type", "text/html; charset=UTF-8");

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Async
    public void sendWelcomeEmail(User user) {

    Map<String, Object> model = Map.of(
        "name", user.getName(),
        "email", user.getEmail()
    );

    sendTemplateEmail(
        user.getEmail(),
        "Welcome to Equis!",
        "welcome",
        model
    );
}
}