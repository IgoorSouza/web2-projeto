package com.igorsouza.games.services.mail;

import com.igorsouza.games.dtos.games.GenericGame;
import org.springframework.mail.MailException;

import java.util.List;

public interface MailService {
    void sendDiscountWarningMail(String to, String subject, List<GenericGame> games) throws MailException;
    void sendVerificationMail(String to, String subject, String token) throws MailException;
}
