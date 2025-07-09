package org.rmc.training_platform.exception;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6190178174188968946L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
