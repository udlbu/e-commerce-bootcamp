package com.ecommerce.mail.config;

import com.ecommerce.mail.ActivationEmailSenderImpl;
import com.ecommerce.mail.adapter.MailServiceAdapter;
import com.ecommerce.mail.domain.ActivationEmailSender;
import com.ecommerce.mail.domain.port.MailServicePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Profile("!integration")
@Configuration
public class TemplateEngineConfig implements WebMvcConfigurer {

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setCacheable(false);
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(ClassLoaderTemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    @Bean
    public MailServicePort mailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        return new MailServiceAdapter(javaMailSender, templateEngine);
    }

    @Bean
    public ActivationEmailSender activationMailSender(MailServicePort mailService) {
        return new ActivationEmailSenderImpl(mailService);
    }
}