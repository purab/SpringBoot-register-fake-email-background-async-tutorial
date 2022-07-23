package in.purabtech.registerfakemailasync.controller;

import in.purabtech.registerfakemailasync.model.ConfirmationToken;
import in.purabtech.registerfakemailasync.model.User;
import in.purabtech.registerfakemailasync.repository.ConfirmationTokenRepository;
import in.purabtech.registerfakemailasync.repository.UserRepository;
import in.purabtech.registerfakemailasync.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(value = "*")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value="/register", method = RequestMethod.POST)
    public ResponseEntity<String> registerUser(@RequestBody User user)
    {

        User existingUser = userRepository.findByEmailIdIgnoreCase(user.getEmailId());
        if(existingUser != null)
        {
            return new ResponseEntity<>("This email already exists!", HttpStatus.ALREADY_REPORTED);
        }
        else
        {
            userRepository.save(user);

            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            confirmationTokenRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmailId());
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("purab@purabtech.in");
            mailMessage.setText("To confirm your account, please click here : "
                    +"http://localhost:8080/confirm-account?token="+confirmationToken.getConfirmationToken());

            // send a notification
            try {
                notificationService.sendNotificaitoin(user, mailMessage);
            }catch( Exception e ){
                // catch error
                logger.info("Error Sending Email: " + e.getMessage());
            }
            return new ResponseEntity<>("successful Registration!", HttpStatus.OK);
        }

        //return new ResponseEntity<>("successful Registration!", HttpStatus.OK);
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> confirmUserAccount(@RequestParam("token")String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null)
        {
            User user = userRepository.findByEmailIdIgnoreCase(token.getUser().getEmailId());
            user.setEnabled(true);
            userRepository.save(user);

            return new ResponseEntity<>("account Verified!", HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("The link is invalid or broken!", HttpStatus.NOT_FOUND);
        }

        //return new ResponseEntity<>("The link is invalid or broken!", HttpStatus.NOT_FOUND);
    }

}
