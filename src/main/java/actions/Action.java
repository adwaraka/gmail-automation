package actions;

import javax.mail.*;
import java.util.Properties;

public class Action {
    protected final String GMAIL_USERNAME = "intuitdemo666@gmail.com";
    protected final String GMAIL_PASSWORD = "1234intuit";
    protected final String GMAIL_IMAP_HOST = "imap.gmail.com";
    protected final String GMAIL_REC_USERNAME = "intuitdemo667@gmail.com";
    protected final String GMAIL_REC_PASSWORD = "1234intuit";
    protected Store store;

    protected Session getSession(final String username, final String password, Properties props) {
        return Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }});
    }

    protected Properties getProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        return props;
    }

    protected Store getStore(String username, String password, String mailbox) throws MessagingException {
        Properties props = getProperties();
        Session session = getSession(username, password, props);
        Store store = session.getStore("imaps");
        store.connect(mailbox, username, password);
        return store;
    }

    protected Folder getReceiveMailFolder(String folderName) throws MessagingException {
        getReceiveMailCredentials();
        Folder folder = this.store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    private void getReceiveMailCredentials() throws MessagingException {
        String username, password, mailbox;
        username = this.GMAIL_REC_USERNAME;
        password = this.GMAIL_REC_PASSWORD;
        mailbox = this.GMAIL_IMAP_HOST;

        this.store = getStore(username, password, mailbox);
    }

    public String senderEmail() {
        String senderEmail;
        senderEmail = this.GMAIL_USERNAME;
        return senderEmail;
    }
}
