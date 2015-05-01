package com.teamd.taxi.service.email;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailTLS {

    final Properties props;
    private Session session;


    public SendMailTLS(String username, String password) {
        this.props = new Properties();
        this.props.put("mail.smtp.auth", "true");
        this.props.put("mail.smtp.starttls.enable", "true");
        this.props.put("mail.smtp.host", "smtp.gmail.com");
        this.props.put("mail.smtp.port", "587");
        this.session = createSession(username, password);
    }


    public SendMailTLS(String username, String password, Properties props) {
        this.props = props;
        this.session = createSession(username, password);
    }


    public void createSendMessage(String subject, String text, String from, String to) throws MessagingException, AddressException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setContent(text, "text/html");
        Transport.send(msg);
    }

    // ��������� ��������� ��� ��������� ������� � ����������� �����
    public void createSendMessageForAll(String subject, String text, String from, List<String> toAll) throws AddressException, MessagingException {
        for (String to : toAll) {
            createSendMessage(subject, text, from, to);

        }
    }

    private Session createSession(String username, String password) {
        return Session.getDefaultInstance(this.props, createAuthenticator(username, password));
    }

    public MimeMessage createMessage(String subject, String from, String to) throws AddressException, MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        return msg;
    }

    public MimeMessage setTextHTML(MimeMessage mess, String text) throws MessagingException {
        mess.setContent(text, "text/html");
        return mess;
    }


    public void sendMessage(MimeMessage mess) throws MessagingException {
        Transport.send(mess);
    }

    private Authenticator createAuthenticator(final String username, final String password) {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }

}
 


