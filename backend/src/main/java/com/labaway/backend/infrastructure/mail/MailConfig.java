package com.labaway.backend.infrastructure.mail;

import com.labaway.backend.configuration.properties.MailProperties;
import com.labaway.backend.configuration.properties.SmtpProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    private final MailProperties mailProperties;
    private final SmtpProperties smtpProperties;

    public MailConfig(
            MailProperties mailProperties,
            SmtpProperties smtpProperties
    ) {
        this.mailProperties = mailProperties;
        this.smtpProperties = smtpProperties;
    }

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailProperties.host());
        mailSender.setPort(mailProperties.port());

        mailSender.setUsername(smtpProperties.username());
        mailSender.setPassword(smtpProperties.password());

        Properties props = mailSender.getJavaMailProperties();

        props.put(
                "mail.transport.protocol",
                mailProperties.protocol()
        );

        props.put(
                "mail.smtp.auth",
                mailProperties.auth()
        );

        props.put(
                "mail.smtp.starttls.enable",
                mailProperties.starttlsEnabled()
        );

        props.put(
                "mail.debug",
                mailProperties.debug()
        );

        return mailSender;
    }
}