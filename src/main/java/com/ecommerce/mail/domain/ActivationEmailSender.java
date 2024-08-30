package com.ecommerce.mail.domain;

import com.ecommerce.mail.domain.model.ActivationMail;

public interface ActivationEmailSender {
    void send(ActivationMail activationMail);
}
