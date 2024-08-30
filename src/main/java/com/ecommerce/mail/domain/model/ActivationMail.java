package com.ecommerce.mail.domain.model;

public record ActivationMail(String user, String recipient, String subject, String token) {

    public static ActivationMail of(String user, String recipient, String token) {
        return new ActivationMail(user, recipient, "Account activation", token);
    }
}
