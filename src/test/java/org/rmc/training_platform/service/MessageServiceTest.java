package org.rmc.training_platform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageService messageService;

    @Test
    void get_shouldReturnFormattedMessage() {
        String key = "greeting";
        String pattern = "Hello, {0}!";
        when(this.messageSource.getMessage(key, null, Locale.getDefault())).thenReturn(pattern);

        String result = this.messageService.get(key, "John");

        assertEquals("Hello, John!", result);
        verify(this.messageSource).getMessage(key, null, Locale.getDefault());
    }

    @Test
    void get_shouldReturnPatternIfNoArgsProvided() {
        String key = "welcome";
        String pattern = "Welcome!";
        when(this.messageSource.getMessage(key, null, Locale.getDefault())).thenReturn(pattern);

        String result = this.messageService.get(key);

        assertEquals("Welcome!", result);
        verify(this.messageSource).getMessage(key, null, Locale.getDefault());
    }

}
