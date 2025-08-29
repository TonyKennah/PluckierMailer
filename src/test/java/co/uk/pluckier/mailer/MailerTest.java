package co.uk.pluckier.mailer;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class MailerTest {

    private Properties testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new Properties();
        testConfig.setProperty("mail.smtp.host", "smtp.test.com");
        testConfig.setProperty("mail.smtp.user", "testuser");
        testConfig.setProperty("mail.smtp.password", "testpass");
        testConfig.setProperty("mail.from.address", "from@test.com");
    }

    @Test
    void run_shouldConstructAndSendCorrectEmail() throws MessagingException, IOException {
        // ARRANGE
        String[] recipients = {"to@example.com"};
        String subject = "Test Subject";
        String body = "Test Body";

        // We need to mock the static Transport.send method as we don't want to send a real email
        try (MockedStatic<Transport> mockedTransport = Mockito.mockStatic(Transport.class)) {
            ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

            // ACT
            // Use the test-only constructor to inject our fake configuration
            Mailer mailer = new Mailer(testConfig, recipients, subject, body);
            mailer.run();

            // VERIFY that Transport.send() was called with a message
            mockedTransport.verify(() -> Transport.send(messageCaptor.capture()));
            Message capturedMessage = messageCaptor.getValue();

            // ASSERT that the captured message has the correct details
            assertEquals(subject, capturedMessage.getSubject());
            assertEquals(body, capturedMessage.getContent().toString());
            assertEquals("from@test.com", capturedMessage.getFrom()[0].toString());
            assertEquals("to@example.com", capturedMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        }
    }
}