package edu.temple.sp_res_lib.email;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.security.Security;
import java.util.Properties;

public class GmailSender {

    private static final String MAIL_HOST = "smtp.gmail.com";
    private static final String MAIL_PORT = "465";
    private static final String MAIL_AUTH = "true";

    private Session session;
    private Multipart _multipart = new MimeMultipart();

    static {
        Security.addProvider(new edu.temple.sp_res_lib.email.JSSEProvider());
    }

    public GmailSender(final String user, final String password) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.host", MAIL_HOST);
        props.put("mail.smtp.auth", MAIL_AUTH);
        props.put("mail.smtp.port", MAIL_PORT);
        props.put("mail.smtp.socketFactory.port", MAIL_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.quitwait", "false");

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
    }

    public void sendMail(String mailTo, String mailFrom, String subject,
                         String body) throws Exception {
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(),
                "text/plain"));

        message.setSender(new InternetAddress(mailFrom));
        message.setSubject(subject);
        message.setDataHandler(handler);

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);
        _multipart.addBodyPart(messageBodyPart);

        // Put parts in message
        message.setContent(_multipart);
        if (mailTo.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));

        Transport.send(message);
    }

    public void addAttachment(String filepath, String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filepath);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        _multipart.addBodyPart(messageBodyPart);
    }

}