package org.rmc.training_platform.exception;

import java.io.Serial;

public class DuplicateFieldException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8823774490988964378L;

    public DuplicateFieldException(String message) {
        super(message);
    }

}
