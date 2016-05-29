package actions;

import javax.mail.*;
import java.io.IOException;
import org.apache.log4j.Logger;

public class ReceiveAction extends Action{
    final static Logger logger = Logger.getLogger(ReceiveAction.class);

    public int getMessageCount() throws IOException, MessagingException {
        Folder inbox = getReceiveMailFolder("inbox");
        int messageCount = inbox.getMessageCount();
        inbox.close(true);
        this.store.close();
        logger.info("Total number of messages in the gmail: " + messageCount);
        return messageCount;
    }

    public String getReqMessageSender(int count) throws IOException, MessagingException {
        Folder inbox = getReceiveMailFolder("inbox");
        Message[] messages = inbox.getMessages();
        String lastSender;
        lastSender = messages[count-1].getFrom()[0].toString();
        inbox.close(true);
        this.store.close();
        return lastSender;
    }


    public String getReqMessageSubject(int count) throws IOException, MessagingException {
        Folder inbox = getReceiveMailFolder("inbox");
        Message[] messages = inbox.getMessages();
        String lastSubject;
        lastSubject = messages[count-1].getSubject();
        inbox.close(true);
        this.store.close();
        return lastSubject;
    }

    public String getReqMessageBody(int count) throws IOException, MessagingException {
        Folder inbox = getReceiveMailFolder("inbox");
        Message[] messages = inbox.getMessages();
        String lastBody;
        lastBody = getText(messages[count - 1]);
        inbox.close(true);
        this.store.close();
        return lastBody;
    }

    public boolean checkReqMsgAttachment(int count) throws IOException, MessagingException {
        Folder inbox = getReceiveMailFolder("inbox");
        Message[] messages = inbox.getMessages();
        Message msg = messages[count - 1];
        if (msg.isMimeType("multipart/mixed")) {
            Multipart mp = (Multipart)msg.getContent();
            if (mp.getCount() > 1) return true;
        }
        return false;
    }

    private String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            return (String)p.getContent();
        }
        if (p.isMimeType("multipart/alternative")) {
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) { text = getText(bp); }
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null) return s;
                } else { return getText(bp); }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null) return s;
            }
        }
        return null;
    }

}
