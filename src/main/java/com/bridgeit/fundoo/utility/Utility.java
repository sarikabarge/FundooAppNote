package com.bridgeit.fundoo.utility;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;

public class Utility {

	@Autowired
	TokenUtil tokenUtil;

	public static void send(String toEmail, String subject, String body) {
		final String fromEmail = "sarikabarge111@gmail.com"; // requires valid gmail id
		final String password = "barge@123"; // correct password for gmail id

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
		props.put("mail.smtp.port", "587"); // TLS Port
		props.put("mail.smtp.auth", "true"); // enable authentication
		props.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS

		// to check email sender credentials are valid or not
		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};

		javax.mail.Session session = Session.getInstance(props, auth);

		try {
			MimeMessage message = new MimeMessage(session);

			message.addHeader("Content-type", "text/HTML; charset=UTF-8");
			message.addHeader("format", "flowed");
			message.addHeader("Content-Transfer-Encoding", "8bit");

			message.setFrom(new InternetAddress("no_reply@gmail.com", "NoReply"));

			message.setReplyTo(InternetAddress.parse("sarikabarge111@gmail.com", false));

			message.setSubject(subject, "UTF-8");

			message.setText(body, "UTF-8");

			message.setSentDate(new Date());

			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			Transport.send(message);

			System.out.println("Email Sent Successfully.........");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getUrl(Long id) {

		TokenUtil tokenUtil = new TokenUtil();

		return "http://localhost:8122/user/" + tokenUtil.createToken(id);
	}

}