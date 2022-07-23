package in.purabtech.registerfakemailasync.service;

import in.purabtech.registerfakemailasync.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private JavaMailSender javaMailSender;

    @Autowired
    public NotificationService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendNotificaitoin(User user, SimpleMailMessage mailMessage) throws MailException, InterruptedException {

        System.out.println("Sleeping now...");
        Thread.sleep(10000);
        System.out.println("Sending email...");

        javaMailSender.send(mailMessage);

        System.out.println("Email Sent!");
    }
}
