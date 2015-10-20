package com.d3bug.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.d3bug.saver.Saver;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPAddressSucceededException;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPTransport;

public class EmailSender {
	
	private static EmailSender instance = new EmailSender();
	public static EmailSender getInstance() { return instance; }
	
	private String from;
	private String mailer;
	private String mailhost;
	private boolean fakeEmail;
	private String bcc;
	
	private String user = "";
	private String password = "";
	private boolean verbose = false;
	private boolean auth = false;	
	private String prot = "smtp";
	
	public void setMailerSettings(String from,String mailer,String mailHost, boolean fakeEmail, String bcc) {
		this.from = from;
		this.mailer = mailer;
		this.mailhost = mailHost;
		this.fakeEmail = fakeEmail;
		this.bcc = bcc;
		System.out.println("**MAILER SETTINGS**");
		System.out.println("From -> "+from);		
		System.out.println("Mailer -> "+mailer);		
		System.out.println("Mailhost -> "+mailhost);		
		System.out.println("Fake Email -> "+fakeEmail);		
		System.out.println("BCC -> "+bcc);		
	}
	
	public void send(Email email) {
		Saver.getInstance().persist(email);
		String to = email.getTo();
		String subject = email.getSubject();
		String body = email.getBody();
		
		if (fakeEmail) {
			writeMailToConsole(to,subject,body);
			return;
		}

		String cc = null;
		
		try {
			Properties props = System.getProperties();
			if (mailhost != null)
				props.put("mail." + prot + ".host", mailhost);
			if (auth)
				props.put("mail." + prot + ".auth", "true");

			// Get a Session object
			Session session = Session.getInstance(props, null);

			// construct the message
			Message msg = new MimeMessage(session);
			if (from != null)
				msg.setFrom(new InternetAddress(from));
			else
				msg.setFrom();

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
					to, false));
			if (cc != null)
				msg.setRecipients(Message.RecipientType.CC, InternetAddress
						.parse(cc, false));
			if (bcc != null)
				msg.setRecipients(Message.RecipientType.BCC, InternetAddress
						.parse(bcc, false));

			msg.setSubject(subject);
			msg.setText(body);

			msg.setHeader("X-Mailer", mailer);
			msg.setSentDate(new Date());

			// send the thing off
			/*
			 * The simple way to send a message is this:
			 * 
			 * Transport.send(msg);
			 * 
			 * 
			 * But we're going to use some SMTP-specific features for
			 * demonstration purposes so we need to manage the Transport object
			 * explicitly.
			 */
			SMTPTransport t = (SMTPTransport) session.getTransport(prot);
			try {
				if (auth)
					t.connect(mailhost, user, password);
				else
					t.connect();
				t.sendMessage(msg, msg.getAllRecipients());
			} finally {
				t.close();
			}

			System.out.println("\nMail was sent successfully.");

		} catch (Exception e) {
			if (e instanceof SendFailedException) {
				MessagingException sfe = (MessagingException) e;
				if (sfe instanceof SMTPSendFailedException) {
					SMTPSendFailedException ssfe = (SMTPSendFailedException) sfe;
					System.out.println("SMTP SEND FAILED:");
					if (verbose)
						System.out.println(ssfe.toString());
					System.out.println("  Command: " + ssfe.getCommand());
					System.out.println("  RetCode: " + ssfe.getReturnCode());
					System.out.println("  Response: " + ssfe.getMessage());
				} else {
					if (verbose)
						System.out.println("Send failed: " + sfe.toString());
				}

				Exception ne;
				while ((ne = sfe.getNextException()) != null
						&& ne instanceof MessagingException) {
					sfe = (MessagingException) ne;
					if (sfe instanceof SMTPAddressFailedException) {
						SMTPAddressFailedException ssfe = (SMTPAddressFailedException) sfe;
						System.out.println("ADDRESS FAILED:");
						if (verbose)
							System.out.println(ssfe.toString());
						System.out.println("  Address: " + ssfe.getAddress());
						System.out.println("  Command: " + ssfe.getCommand());
						System.out.println("  RetCode: " + ssfe.getReturnCode());
						System.out.println("  Response: " + ssfe.getMessage());
					} else if (sfe instanceof SMTPAddressSucceededException) {
						System.out.println("ADDRESS SUCCEEDED:");
						SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException) sfe;
						if (verbose)
							System.out.println(ssfe.toString());
						System.out.println("  Address: " + ssfe.getAddress());
						System.out.println("  Command: " + ssfe.getCommand());
						System.out.println("  RetCode: " + ssfe.getReturnCode());
						System.out.println("  Response: " + ssfe.getMessage());
					}
				}
			} else {
				System.out.println("Got Exception: " + e);
				if (verbose)
					e.printStackTrace();
			}
		}
	}


	private void writeMailToConsole(String to, String subject, String body) {
		System.out.println("*EMAIL***EMAIL***EMAIL***EMAIL***EMAIL***EMAIL***EMAIL***");
		System.out.println("From: "+from);
		System.out.println("To: "+to);
		System.out.println("Subject: "+subject);
		System.out.println("");
		System.out.println(body);
		System.out.println("*EMAIL***EMAIL***EMAIL***EMAIL***EMAIL***EMAIL***EMAIL***");
	}

}
