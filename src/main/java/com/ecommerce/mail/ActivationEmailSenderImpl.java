package com.ecommerce.mail;

import com.ecommerce.mail.domain.ActivationEmailSender;
import com.ecommerce.mail.domain.model.ActivationMail;
import com.ecommerce.mail.domain.port.MailServicePort;
import org.springframework.scheduling.annotation.Async;

public class ActivationEmailSenderImpl implements ActivationEmailSender {

    private final MailServicePort mailService;

    public ActivationEmailSenderImpl(MailServicePort mailService) {
        this.mailService = mailService;
    }

    @Async
    public void send(ActivationMail activationMail) {
        mailService.sendMail(activationMail);
    }
}
