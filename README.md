[![Build & Test Status](https://github.com/TonyKennah/PluckierMailer/actions/workflows/maven.yml/badge.svg)](https://github.com/TonyKennah/PluckierMailer/actions/workflows/maven.yml)

# PluckierMailer
A library which allows emails to be sent for forgotten password, registration welcome, etc.

### Needs
db.properties file
```properties
mail.smtp.host=HOST
mail.smtp.port=NUMBER
mail.smtp.user=USER
mail.smtp.password=PASSWORD
mail.from.address=INFO@WHATEVER.COM
```

Example

```java
public class PasswordResetService {

    public void sendPasswordResetEmail(String user, String email, String uuid) throws Exception {
        // Construct the email content (business logic)
        String subject = "Pluckier Forgotten Password";
        String body = "Pluckier Forgotten Password\n\n" + "\tUsername: \t" + user + "\n\n\n"
				+ "If you have forgotten your password please click this link to reset it:\n\n"
				+ " http://www.pluckier.co.uk/forgotten/?qui=" + uuid + "\n\n"
				+ "This link will become invalid after first use or 24 hours. \n\n"
				+ "Please keep this email as reference for your username details.  We hope Pluckier provides \n"
				+ "you with the racing information you need. \n\n"
				+ "Pick lucky, be Pluckier \nhttp://www.pluckier.co.uk \n\n\n\n\n" + "Terms & Conditions \n\n"
				+ "1) Pluckier ( \"Pluckier\" being the providers of https://www.pluckier.co.uk )."
				+ "With regard all dealings of consumers of the Pluckier service and its operations"
				+ ", I (I being the consumer of this service) have no hold or rights or say in anything Pluckier "
				+ "does or not with regard the current or future state of its service.\n"
				+ "2) By clicking the link above you agree with our efforts to comply with the European "
				+ "Union's new General Data Protection Regulation (GDPR), as well as our own commitment to data "
				+ "privacy.  We therefore have our Privacy Policy available at http://www.pluckier.co.uk/privacy.html \n"
				+ "3) Pluckier accepts no responsibility for monetary losses through gambling!";
        
        String[] recipients = { email };

        // Create and start the Mailer thread. It will load its own configuration.
        new Thread(new Mailer(recipients, subject, body)).start();
    }
}
```

```
<dependency>
  <groupId>co.uk.pluckier.mailer</groupId>
  <artifactId>mailer</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
