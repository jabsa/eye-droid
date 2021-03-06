package notifier;
import java.util.Date; 
import java.util.Properties; 
import javax.activation.CommandMap; 
import javax.activation.DataHandler; 
import javax.activation.DataSource; 
import javax.activation.FileDataSource; 
import javax.activation.MailcapCommandMap; 
import javax.mail.BodyPart; 
import javax.mail.Multipart; 
import javax.mail.PasswordAuthentication; 
import javax.mail.Session; 
import javax.mail.Transport; 
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeBodyPart; 
import javax.mail.internet.MimeMessage; 
import javax.mail.internet.MimeMultipart; 


public class Mail extends javax.mail.Authenticator { 
  private String email; 
  private String passd; 

  private String[] to; 
  private String from; 

  private String port; 
  private String sPort; 

  private String subject; 
  private String body; 

  private boolean debugable; 

	private Multipart multipart; 


  public Mail() { 
    port = "465"; // default smtp port 
    sPort = "465"; // default socketfactory port 

    email = ""; // username 
    passd = ""; // password 
    from = ""; // email sent from 
    subject = ""; // email subject 
    body = ""; // email body 

    debugable = false; // debug mode on or off - default off 

    multipart = new MimeMultipart(); 

    // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added. 
    MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
    CommandMap.setDefaultCommandMap(mc); 
  } 

  public Mail(String user, String pass) { 
    this(); 

    email = user; 
    passd = pass; 
  } 

  public boolean send() throws Exception { 
    Properties props = _setProperties(); 

    if(!email.equals("") && !passd.equals("") && to.length > 0 && !from.equals("") && !subject.equals("") && !body.equals("")) { 
      Session session = Session.getInstance(props, this); 

      MimeMessage msg = new MimeMessage(session); 

      msg.setFrom(new InternetAddress(from)); 

      InternetAddress[] addressTo = new InternetAddress[to.length]; 
      for (int i = 0; i < to.length; i++) { 
        addressTo[i] = new InternetAddress(to[i]); 
      } 
        msg.setRecipients(MimeMessage.RecipientType.TO, addressTo); 

      msg.setSubject(subject); 
      msg.setSentDate(new Date()); 

      // setup message body 
      BodyPart messageBodyPart = new MimeBodyPart(); 
      messageBodyPart.setText(body); 
      multipart.addBodyPart(messageBodyPart); 

      // Put parts in message 
      msg.setContent(multipart); 

      // send email 
      Transport.send(msg); 

      return true; 
    } else { 
      return false; 
    } 
  } 

  public void addAttachment(String filename) throws Exception { 
    BodyPart messageBodyPart = new MimeBodyPart(); 
    DataSource source = new FileDataSource(filename); 
    messageBodyPart.setDataHandler(new DataHandler(source)); 
    messageBodyPart.setFileName(filename); 

    multipart.addBodyPart(messageBodyPart); 
  } 
 
  @Override 
  public PasswordAuthentication getPasswordAuthentication() { 
    return new PasswordAuthentication(email, passd); 
  } 

  private Properties _setProperties() { 
    Properties props = new Properties(); 
    
    if(debugable) { 
      props.put("mail.debug", "true"); 
    } 
    /*
    if(_auth) { 
      props.put("mail.smtp.auth", "true"); 
    } */
    props.put("mail.smtp.auth", "true");
	props.put("mail.smtp.starttls.enable", "true");
	props.put("mail.smtp.host", "smtp.gmail.com");
	props.put("mail.smtp.port", port);
	
    props.put("mail.smtp.socketFactory.port", sPort); 
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
    props.put("mail.smtp.socketFactory.fallback", "false");

    return props; 
  } 

  // the getters and setters 
  public String getBody() { 
    return body; 
  } 

  public void setBody(String _body) { 
    this.body = _body; 
  }

  public void setTo(String[] toArr) {
      // TODO Auto-generated method stub
      this.to=toArr;
  }

  public void setFrom(String string) {
      // TODO Auto-generated method stub
      this.from=string;
  }

  public void setSubject(String string) {
      // TODO Auto-generated method stub
      this.subject=string;
  }  
  
}