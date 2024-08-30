package com.ecommerce.mail.adapter;

import com.ecommerce.mail.domain.model.ActivationMail;
import com.ecommerce.mail.domain.port.MailServicePort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class MailServiceAdapter implements MailServicePort {

    private final static Logger LOG = LoggerFactory.getLogger(MailServiceAdapter.class);
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Value("${domain.host}")
    private String host;

    public MailServiceAdapter(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendMail(ActivationMail activationMail) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom(fromMail);
            mimeMessageHelper.setTo(activationMail.recipient());
            mimeMessageHelper.setSubject(activationMail.subject());
            Context context = new Context();
            context.setVariable("activationLink", link(activationMail.token()));
            context.setVariable("user", activationMail.user());
            String processedString = templateEngine.process("activation-mail", context);
            mimeMessageHelper.setText(processedString, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            LOG.error("Error while sending activation email to <" + activationMail.recipient() + ">", ex);
        }
    }

    private String link(String token) {
        return host + "/activate?token=" + token;
    }
}
