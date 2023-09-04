package com.coutinho.userservicemailsender.services;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.File;
import java.util.Map;

import static com.coutinho.userservicemailsender.utils.EmailUtils.getEmailMessage;
import static com.coutinho.userservicemailsender.utils.EmailUtils.getVerificationUrl;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService{

    private static final String NEW_USER_ACCOUNT_VERIFICATION = "new user account verify";
    private static final String UTF_8_ENCODING = "UTF-8";
    private static final String EMAIL_TEMPLATE = "emailtemplate";
    public static final String TEXT_HTML_ENCODING = "text/html";

    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Override
    @Async
    public void sendSimpleMailMessage(String name, String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(getEmailMessage(name, host, token));
            mailSender.send(message);
        }catch (Exception exception){
            log.info(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
        // Sending simple mail message
    }

    @Override
    @Async
    public void sendMimeMessageWithAttachments(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING); // Specify UTF-8 encoding
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(getEmailMessage(name, host, token));

            // Add attachments
            FileSystemResource timer = new FileSystemResource(new File(System.getProperty("user.home") + "/downloads/mail/timer.jpg"));
            FileSystemResource trent = new FileSystemResource(new File(System.getProperty("user.home") + "/downloads/mail/trent.jpg"));
            FileSystemResource task = new FileSystemResource(new File(System.getProperty("user.home") + "/downloads/mail/Task.docx"));

            helper.addAttachment(timer.getFilename(), timer);
            helper.addAttachment(trent.getFilename(), trent);
            helper.addAttachment(task.getFilename(), task);

            mailSender.send(message);
        } catch (Exception exception) {
            log.error("Error sending email: {}", exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage(), exception);
        }
        // Sending mail with attachment
    }



    @Override
    @Async
    public void sendMimeMessageWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING); // Specify UTF-8 encoding
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(getEmailMessage(name, host, token));

            FileSystemResource timer = new FileSystemResource(new File(System.getProperty("user.home") + "/downloads/mail/timer.jpg"));
            FileSystemResource trent = new FileSystemResource(new File(System.getProperty("user.home") + "/downloads/mail/trent.jpg"));
            FileSystemResource task = new FileSystemResource(new File(System.getProperty("user.home") + "/downloads/mail/Task.docx"));

            helper.addInline(getContentId(timer.getFilename()), timer);
            helper.addInline(getContentId(trent.getFilename()), trent);
            helper.addInline(getContentId(task.getFilename()), task);

            mailSender.send(message);
        } catch (Exception exception) {
            log.error("Error sending email: {}", exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage(), exception);
        }
        // Sending mail with embedded files
    }

    @Override
    public void sendHtmlEmail(String name, String to, String token) {
        try {
            Context context = new Context();
//            context.setVariable("name", name);
//            context.setVariable("url", getVerificationUrl(host, token));
            context.setVariables(Map.of("name", name, "url", getVerificationUrl(host, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);

            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING); // Specify UTF-8 encoding
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (Exception exception) {
            log.error("Error sending email: {}", exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage(), exception);
        }
        // Sending mail using Html
    }

    @Override
    @Async
    public void sendHtmlEmailWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING); // Specify UTF-8 encoding
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
//            helper.setText(text, true);

            Context context = new Context();
            context.setVariables(Map.of("name", name, "url", getVerificationUrl(host, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);

            // Add Html email body
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, TEXT_HTML_ENCODING);
            mimeMultipart.addBodyPart(messageBodyPart);

            // Add images to the email body
            BodyPart imageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource(System.getProperty("user.home") + "/downloads/mail/trent.jpg");
            imageBodyPart.setDataHandler(new DataHandler(dataSource));
            imageBodyPart.setHeader("Content-ID", "image");
            mimeMultipart.addBodyPart(imageBodyPart);

            message.setContent(mimeMultipart);
            mailSender.send(message);
        } catch (Exception exception) {
            log.error("Error sending email: {}", exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage(), exception);
        }

    }

    private MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }

    private String  getContentId(String filename){
        return "<" + filename + ">";
    }
}
