package com.teamd.taxi.service.email;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class MailService {

    private Properties props;
    private Session session;

    public MailService(final String username, final String password, Properties props) {
        this.props = props;
        this.session = Session.getDefaultInstance(this.props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendMessage(String subject, String body, String from, String to) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setSubject(subject, "UTF-8");
        msg.setContent(body, "text/html; charset=UTF-8");
        msg.setFrom(new InternetAddress(from));
        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        Transport.send(msg);
    }

    public void sendNotification(String to, Notification pattern, Object... args) throws MessagingException {
        try {
            sendMessage("Taxi service system notification", pattern.getBody(args), "netcrackerdteam@gmail.com", to);
        } catch (MessagingException ex) {
            System.err.println(ex);
        }
    }

    public void sendMessageForAll(String subject, String body, String from, List<String> recipients) throws AddressException, MessagingException {
        for (String to : recipients) {
            sendMessage(subject, body, from, to);
        }
    }
}