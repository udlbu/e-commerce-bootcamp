package com.ecommerce.shared.config;

import com.ecommerce.mail.domain.ActivationEmailSender;
import com.ecommerce.mail.domain.model.ActivationMail;

import java.util.ArrayList;
import java.util.List;

public class InMemoryActivationEmailSender implements ActivationEmailSender {

    public final List<ActivationMail> mails = new ArrayList<>();

    @Override
    public void send(ActivationMail activationMail) {
        mails.add(activationMail);
    }
}