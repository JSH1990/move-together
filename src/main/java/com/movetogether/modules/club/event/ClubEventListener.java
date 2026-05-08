package com.movetogether.modules.club.event;

import com.movetogether.infra.config.AppProperties;
import com.movetogether.infra.mail.EmailService;
import com.movetogether.modules.acount.AccountRepository;
import com.movetogether.modules.club.ClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class ClubEventListener {

    private final ClubRepository clubRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
}
