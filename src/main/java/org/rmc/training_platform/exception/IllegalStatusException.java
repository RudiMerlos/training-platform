package org.rmc.training_platform.exception;

import java.io.Serial;

public class IllegalStatusException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8212278303567448810L;

    public IllegalStatusException(String message) {
        super(message);
    }

}
