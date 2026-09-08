package com.zenika.zenikia.shared.domain;

/**
 * Base unchecked exception for every business error raised by the domain or
 * application layers. Never depends on Spring or any provider SDK.
 */
public abstract class ZenikiaException extends RuntimeException {

    protected ZenikiaException(String message) {
        super(message);
    }

    protected ZenikiaException(String message, Throwable cause) {
        super(message, cause);
    }
}
