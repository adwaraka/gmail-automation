package actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendAction extends Action{
    public void sendMail(String sendTo, String subjectLine, String msgBody, Boolean attachmentFlag)
            throws IOException, MessagingException, URISyntaxException {
        String username, password;
        username = this.GMAIL_USERNAME;
        password = this.GMAIL_PASSWORD;
        Properties props = getProperties();
        Session session = getSession(username, password, props);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));
        msg.setSubject(subjectLine);

        if (attachmentFlag) {
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(msgBody);
            messageBodyPart.setContent(msgBody, "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            URL url = this.getClass().getClassLoader().getResource("jpeg_desert.jpg");
            if (url!=null) {
                messageBodyPart = new MimeBodyPart();
                messageBodyPart.attachFile(new File(url.toURI()));
                multipart.addBodyPart(messageBodyPart);
                msg.setContent(multipart);
            } else {
                throw new FileNotFoundException("Attachment folder missing on the local machine.");
            }
        } else {
            msg.setText(msgBody);
        }
        Transport.send(msg);
    }
}
