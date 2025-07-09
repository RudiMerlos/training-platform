package org.rmc.training_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String get(String key, Object... args) {
        String pattern = this.messageSource.getMessage(key, null, Locale.getDefault());
        return MessageFormat.format(pattern, args);
    }

}
