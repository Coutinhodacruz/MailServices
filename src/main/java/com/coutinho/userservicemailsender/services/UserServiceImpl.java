package com.coutinho.userservicemailsender.services;

import com.coutinho.userservicemailsender.exception.EmailAlreadyExistException;
import com.coutinho.userservicemailsender.model.Confirmation;
import com.coutinho.userservicemailsender.model.User;
import com.coutinho.userservicemailsender.repository.ConfirmationRepository;
import com.coutinho.userservicemailsender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final ConfirmationRepository confirmationRepository;

    private final EmailService emailService;

    @Override
    public User savedUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyExistException("Email already exists");
        }
        user.setEnabled(false);
        userRepository.save(user);

        Confirmation confirmation = new Confirmation();
        confirmationRepository.save(confirmation);

//        emailService.sendSimpleMailMessage(user.getName(), user.getEmail(), confirmation.getToken());
//        emailService.sendMimeMessageWithAttachments(user.getName(), user.getEmail(), confirmation.getToken());
//        emailService.sendMimeMessageWithEmbeddedFiles(user.getName(), user.getEmail(), confirmation.getToken());
//        emailService.sendHtmlEmail(user.getName(), user.getEmail(), confirmation.getToken());
        emailService.sendHtmlEmailWithEmbeddedFiles(user.getName(), user.getEmail(), confirmation.getToken());
        return user;
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnabled(true);
        userRepository.save(user);

        return Boolean.TRUE;
    }
}
