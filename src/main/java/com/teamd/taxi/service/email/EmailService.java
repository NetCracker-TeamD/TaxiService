package com.teamd.taxi.service.email;

import java.util.HashSet;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.stereotype.Service;

/**
 * Class for sending messages with a standard text. (see. SystemNotification)
 *
 * @author Ivaniv Ivan
 */
@Service
public class EmailService {

    /**
     * User name of email account (who sends message to another addresses)
     */
    final String username = "user-name";
    /**
     * Password of email account
     */
    final String password = "pass";
    /**
     * Standart subject
     */
    final String subject = "Taxi service ";
    private SendMailTLS smt;

    /**
     * Users that are blacklisted
     */
    private static HashSet<String> userBlackListEmail = new HashSet<String>();


    public EmailService() {
        smt = new SendMailTLS(username, password);
    }


    public void notifyAssigned(String email) throws MessagingException, AddressException {
        smt.createSendMessage(subject, SystemNotification.ASSIGNED.getState(), username, email);
    }

    public void notifyAssigned(List<String> emails) throws MessagingException, AddressException {
        smt.createSendMessageForAll(subject, SystemNotification.ASSIGNED.getState(), username, emails);
    }

    public void notifyQueued(String email) throws MessagingException, AddressException {
        smt.createSendMessage(subject, SystemNotification.QUEUED.getState(), username, email);
    }

    public void notifyQueued(List<String> emails) throws MessagingException, AddressException {
        smt.createSendMessageForAll(subject, SystemNotification.QUEUED.getState(), username, emails);
    }

    public void notifyRefused(String email) throws MessagingException, AddressException {
        smt.createSendMessage(subject, SystemNotification.REFUSED.getState(), username, email);
    }

    public void notifyRefused(List<String> emails) throws MessagingException, AddressException {
        smt.createSendMessageForAll(subject, SystemNotification.REFUSED.getState(), username, emails);
    }

    public void notifyInProgress(String email) throws MessagingException, AddressException {
        smt.createSendMessage(subject, SystemNotification.IN_PROGRESS.getState(), username, email);
    }

    public void notifyInProgress(List<String> emails) throws MessagingException, AddressException {
        smt.createSendMessageForAll(subject, SystemNotification.IN_PROGRESS.getState(), username, emails);
    }

    public void notifyCompleted(String email) throws MessagingException, AddressException {
        smt.createSendMessage(subject, SystemNotification.COMPLETED.getState(), username, email);
    }

    public void notifyCompleted(List<String> emails) throws MessagingException, AddressException {
        smt.createSendMessageForAll(subject, SystemNotification.COMPLETED.getState(), username, emails);
    }

    public void notifyUpdated(String email) throws MessagingException, AddressException {
        smt.createSendMessage(subject, SystemNotification.UPDATED.getState(), username, email);
    }

    public void notifyUpdated(List<String> emails) throws MessagingException, AddressException {
        smt.createSendMessageForAll(subject, SystemNotification.UPDATED.getState(), username, emails);
    }

    public void addToBlackList(String email) throws MessagingException, AddressException {
        if (userBlackListEmail.add(email))
            smt.createSendMessage(subject, SystemNotification.BLACK_LIST.getState(), username, email);
    }


    public void addToBlackList(HashSet<String> emails) throws MessagingException, AddressException {
        for (String e : emails) {
            addToBlackList(e);
        }
    }
}
