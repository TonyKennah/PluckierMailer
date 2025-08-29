package co.uk.pluckier.mailer;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class Mailer implements Runnable {

	private Properties mailerConfig = new Properties();
	private final String[] emailList;
	private final String subject;
	private final String body;

	public Mailer(String[] emailList, String subject, String body) throws java.io.IOException {
		this.emailList = emailList;
		this.subject = subject;
		this.body = body;

		// Load configuration internally from the classpath
		Properties config = new Properties();
		try (java.io.InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
			if (input == null) {
				throw new java.io.IOException("db.properties not found on classpath.");
			}
			config.load(input);
		}
		this.mailerConfig = config;
	}

	/**
	 * Constructor for testing purposes, allowing dependency injection of configuration.
	 */
	Mailer(Properties mailerConfig, String[] emailList, String subject, String body) {
		this.mailerConfig = mailerConfig;
		this.emailList = emailList;
		this.subject = subject;
		this.body = body;
	}

	@Override
	public void run()
	{
		boolean debug = false;
		String smtpHost = mailerConfig.getProperty("mail.smtp.host");

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", mailerConfig.getProperty("mail.smtp.port", "587"));
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", smtpHost);
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Authenticator auth = new SMTPAuthenticator(
				mailerConfig.getProperty("mail.smtp.user"),
				mailerConfig.getProperty("mail.smtp.password")
		);
		Session session = Session.getDefaultInstance(props, auth);

		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		try {
			String emailFromAddress = mailerConfig.getProperty("mail.from.address");
			InternetAddress addressFrom = new InternetAddress(emailFromAddress);
			msg.setFrom(addressFrom);
			InternetAddress fromAdd = new InternetAddress(emailFromAddress);
			InternetAddress[] ia = new InternetAddress[1];
			ia[0] = fromAdd;
			msg.setReplyTo(ia);

			InternetAddress[] addressTo = new InternetAddress[emailList.length];
			for (int i = 0; i < emailList.length; i++) {
				addressTo[i] = new InternetAddress(emailList[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			// Setting the Subject and Content Type
			msg.setSubject(subject);
			msg.setContent(body, "text/plain");
			Transport.send(msg);
		} catch (MessagingException e) {
			e.printStackTrace();
			System.out.println("MessagingException " + e);
		}
	}

	/**
	 * SimpleAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	private static class SMTPAuthenticator extends jakarta.mail.Authenticator {
		private final String username;
		private final String password;

		public SMTPAuthenticator(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}
}
