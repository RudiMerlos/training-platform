package org.rmc.training_platform.exception;

import java.io.Serial;

public class DuplicateEmailException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1919298774412032050L;

    public DuplicateEmailException(String message) {
        super(message);
    }

}
