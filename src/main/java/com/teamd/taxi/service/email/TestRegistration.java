package com.teamd.taxi.service.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

public class TestRegistration {

	public static void main(String[] args) {

		
		
		
		//User name of email account (who sends message to another addresses)
		final String username="*******@****", to ="******@*****";
		// Password of email account
		final String password="xxxxxxx"; 
		String subject = "Taxi Service";
		//content: text and html
		String text = "Click on the link: <a href =\"http://www.w3schools.com/html/\">Example</a>";
		
		//Get the session object that stores all the information of host
		//like host name, username, password etc.
		SendMailTLS send = new SendMailTLS(username, password);
		
		
		
		// compose and send message 
		
		// sending to email
		try {
			// sending to one email adresses
			send.createSendMessage(subject, text, username, to);
		} catch ( MessagingException e) {
			e.printStackTrace();
		}
		
		// sending to few email adresses
		List<String> list = new ArrayList<String>();
		list.add("*********@yandex.ua");
		list.add("*********@yandex.ru");
		list.add("*********@gmail.com");

		try {
			send.createSendMessageForAll(subject, text, username, list);
		} catch ( MessagingException e) {
			e.printStackTrace();
		}
	}

}
