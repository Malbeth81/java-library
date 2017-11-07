package malbeth.javautils.mail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Mail {

    public static boolean send(Properties serverConfig, String recipient, String subject, String content) {
        return send(serverConfig, recipient, subject, content, null, false, null);
    }

    public static boolean send(Properties serverConfig, String recipient, String subject, String content, boolean html) {
        return send(serverConfig, recipient, subject, content, null, html, null);
    }

    public static boolean send(Properties serverConfig, String recipient, String subject, String content, List<String> attachments) {
        return send(serverConfig, recipient, subject, content, null, false, attachments);
    }

    public static boolean send(Properties serverConfig, String recipient, String subject, String content, boolean html, List<String> attachments) {
        return send(serverConfig, recipient, subject, content, null, html, attachments);
    }

    public static boolean send(Properties serverConfig, String recipient, String subject, String content, String sender) {
        return send(serverConfig, sender, recipient, subject, content, false, null);
    }

    public static boolean send(Properties serverConfig, String recipient, String subject, String content, String sender, boolean html) {
        return send(serverConfig, sender, recipient, subject, content, html, null);
    }

    public static boolean send(Properties serverConfig, String recipient, String subject, String content, String sender, List<String> attachments) {
        return send(serverConfig, sender, recipient, subject, content, false, attachments);
    }

    public static boolean send(Properties serverConfig, String recipient, String subject, String content, String sender, boolean html, List<String> attachments) {
        if (serverConfig != null && recipient != null && recipient.trim().length() > 0 && subject != null && content != null) {
            try {
                MimeMessage message = new MimeMessage(Session.getDefaultInstance(serverConfig));
                String[] recipients = recipient.split(",");
                for (int i = 0; i < recipients.length; i++)
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients[i]));
                if (sender != null)
                    message.setFrom(new InternetAddress(sender));
                else
                    message.setFrom();
                message.setSubject(subject);

                if (attachments != null && attachments.size() > 0) {
                    Multipart multipart = new MimeMultipart();
                    BodyPart messageBodyPart = new MimeBodyPart();
                    if (html)
                        messageBodyPart.setContent(content, "text/html");
                    else
                        messageBodyPart.setText(content);
                    multipart.addBodyPart(messageBodyPart);
                    for (Iterator<String> it = attachments.iterator(); it.hasNext(); ) {
                        String filename = it.next();
                        BodyPart attachementBodyPart = new MimeBodyPart();
                        attachementBodyPart.setDataHandler(new DataHandler(new FileDataSource(filename)));
                        attachementBodyPart.setFileName(filename);
                        multipart.addBodyPart(attachementBodyPart);
                    }
                    message.setContent(multipart);
                } else if (html)
                    message.setContent(content, "text/html");
                else
                    message.setText(content);

                Transport.send(message);

                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }
}
