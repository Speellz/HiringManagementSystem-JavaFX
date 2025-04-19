package com.BRJavaProject.utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    public static String sendEmail(String toEmail, String subject, String messageText) {
        final String fromEmail = System.getenv("EMAIL_ADDRESS");
        final String password = System.getenv("EMAIL_PASSWORD");

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "false");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);
            return "E-Mail Send Successfully";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "E-Mail Couldn't Send: " + e.getMessage();
        }
    }
}
