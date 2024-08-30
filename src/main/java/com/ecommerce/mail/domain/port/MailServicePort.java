package com.ecommerce.mail.domain.port;

import com.ecommerce.mail.domain.model.ActivationMail;

public interface MailServicePort {

    void sendMail(ActivationMail activationMail);
}
