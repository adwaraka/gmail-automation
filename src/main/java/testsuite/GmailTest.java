package testsuite;

import actions.Action;
import actions.ReceiveAction;
import actions.SendAction;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.*;
import org.junit.rules.TestName;

/*
*  Using Google email as a Web product, how to TEST the 'send' and 'received' functionality.
*  The GmailTest is a test suite that tests the functionality of the basic send & received.
*  It also contains additional testcases that can be used to test various other Gmail APIs.
*/
public class GmailTest {
    private String testEmail;
    private final long timeout = 10000;
    private final long cooldown = 2500;
    final static Logger logger = Logger.getLogger(GmailTest.class);
    @Rule public TestName name = new TestName();

    @BeforeClass
    public static void setUpBeforeClass() throws FileNotFoundException {
        URL url = GmailTest.class.getClassLoader().getResource("log4j.properties");
        if (url!=null) {
            PropertyConfigurator.configure(new FileInputStream(url.getPath()));
        } else {
            BasicConfigurator.configure();
        }
    }

    @Before
    public void setUp() throws InterruptedException {
        this.testEmail = "intuitdemo667@gmail.com";
        Thread.sleep(this.cooldown);
        logger.info("--------- TEST CASE STARTS ----------");
    }

    @After
    public void tearDownUp() throws InterruptedException {
        logger.info("---------- TEST CASE ENDS -----------");
        Thread.sleep(this.cooldown);
    }

    @Test(expected=NullPointerException.class)
    public void testSendActionNullRecipient() throws IOException, MessagingException, URISyntaxException {
        logger.info("Running " +this.name.getMethodName());
        logger.info("Trying with null email recipient.");
        SendAction sendAction = new SendAction();
        sendAction.sendMail(null, "Subject with Attachment", "Test Mail with attachment.", true);
    }

    @Test(expected=javax.mail.internet.AddressException.class)
    public void testSendActionEmptyRecipient() throws IOException, MessagingException, URISyntaxException {
        logger.info("Running " +this.name.getMethodName());
        String testEmail = "";
        logger.info("Trying with empty email recipient: " +testEmail);
        SendAction sendAction = new SendAction();
        sendAction.sendMail(testEmail, "Subject with Attachment", "Test Mail with attachment.", true);
    }

    @Test(expected=SendFailedException.class)
    public void testSendActionInvalidEmail() throws IOException, MessagingException, URISyntaxException {
        logger.info("Running " +this.name.getMethodName());
        String testEmail = "intuitdemo667gmail.com";
        logger.info("Trying with an invalid email: " +testEmail);
        SendAction sendAction = new SendAction();
        sendAction.sendMail(testEmail, "Subject with Attachment", "Test Mail with attachment.", true);
    }

    @Test(expected=NullPointerException.class)
    public void testSendActionNullSubBody() throws IOException, MessagingException, URISyntaxException {
        logger.info("Running " +this.name.getMethodName());
        String testEmail = this.testEmail;
        SendAction sendAction = new SendAction();
        logger.info("Feeding null values into the subject and body fields.");
        sendAction.sendMail(testEmail, null, null, false);
    }

    @Test
    public void testSendActionEmptySubBody() throws IOException, MessagingException, URISyntaxException,
            InterruptedException {
        logger.info("Running " + this.name.getMethodName());
        String testEmail = this.testEmail;
        SendAction sendAction = new SendAction();
        logger.info("Feeding empty values into the subject and body fields.");
        sendAction.sendMail(testEmail, "", "", false);
        Thread.sleep(this.timeout);
        int countOfMsgs;
        ReceiveAction receiveAction = new ReceiveAction();
        countOfMsgs = receiveAction.getMessageCount();
        String getLatestSubject = receiveAction.getReqMessageSubject(countOfMsgs);
        logger.info("Latest subject: " + getLatestSubject);
        assert(getLatestSubject==null);
    }

    @Test
    public void testSendActionHappyPath() throws IOException, MessagingException, URISyntaxException,
            InterruptedException {
        logger.info("Running " +this.name.getMethodName());
        String testEmail = this.testEmail;
        String subject = UUID.randomUUID().toString();
        String body = UUID.randomUUID().toString();
        logger.info("Sent subject line: " + subject);
        logger.info("Sent body line: " + body);
        SendAction sendAction = new SendAction();
        sendAction.sendMail(testEmail, subject, body, false);
        Thread.sleep(this.timeout);
        int countOfMsgs;
        ReceiveAction receiveAction = new ReceiveAction();
        countOfMsgs = receiveAction.getMessageCount();
        String getLatestSubject = receiveAction.getReqMessageSubject(countOfMsgs);
        String getLatestSender = receiveAction.getReqMessageSender(countOfMsgs);
        logger.info("Latest sender: " +getLatestSender);
        logger.info("Latest subject: " +getLatestSubject);
        String getLatestMessage = receiveAction.getReqMessageBody(countOfMsgs);
        logger.info("Latest message: " + getLatestMessage);
        Boolean hasAttachment = receiveAction.checkReqMsgAttachment(countOfMsgs);
        logger.info("Attachment present: "+hasAttachment);
        Action action = new Action();
        assert(getLatestSender.equals(action.senderEmail()) && getLatestSubject.equals(subject) && getLatestMessage.contains(body) && !hasAttachment);
    }

    @Test
    public void testSendActionWithAttachment() throws IOException, MessagingException, URISyntaxException,
            InterruptedException {
        logger.info("Running " +this.name.getMethodName());
        String testEmail = this.testEmail;
        String subject = UUID.randomUUID().toString();
        String body = UUID.randomUUID().toString();
        logger.info("Sent subject line: " + subject);
        logger.info("Sent body line: " + body);
        SendAction sendAction = new SendAction();
        sendAction.sendMail(testEmail, subject, body, true);
        Thread.sleep(this.timeout);
        int countOfMsgs;
        ReceiveAction receiveAction = new ReceiveAction();
        countOfMsgs = receiveAction.getMessageCount();
        String getLatestSubject = receiveAction.getReqMessageSubject(countOfMsgs);
        logger.info("Latest subject: " +getLatestSubject);
        String getLatestMessage = receiveAction.getReqMessageBody(countOfMsgs);
        logger.info("Latest message: " +getLatestMessage);
        Boolean hasAttachment = receiveAction.checkReqMsgAttachment(countOfMsgs);
        logger.info("Attachment present: "+hasAttachment);
        assert(getLatestSubject.equals(subject) && getLatestMessage.contains(body) && hasAttachment);
    }

    @Test
    public void testMessageCounter() throws IOException, MessagingException, URISyntaxException,
            InterruptedException {
        logger.info("Running " +this.name.getMethodName());
        int countOfMsgs, newCountOfMsgs;
        ReceiveAction receiveAction = new ReceiveAction();
        countOfMsgs = receiveAction.getMessageCount();
        String testEmail = this.testEmail;
        String subject = UUID.randomUUID().toString();
        String body = UUID.randomUUID().toString();
        logger.info("Sent subject line: " + subject);
        logger.info("Sent body line: " + body);
        SendAction sendAction = new SendAction();
        sendAction.sendMail(testEmail, subject, body, false);
        Thread.sleep(this.timeout);
        newCountOfMsgs = receiveAction.getMessageCount();
        String lastRecdSub = receiveAction.getReqMessageSubject(newCountOfMsgs);
        String lastRecdBody = receiveAction.getReqMessageBody(newCountOfMsgs);
        logger.info("Last subject line: " +lastRecdSub);
        logger.info("Last body line: " + lastRecdBody);
        assert(newCountOfMsgs==countOfMsgs+1 && subject.equals(lastRecdSub) && lastRecdBody.contains(body));
    }
}
