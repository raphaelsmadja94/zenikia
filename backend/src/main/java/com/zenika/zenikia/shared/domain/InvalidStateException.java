package com.zenika.zenikia.shared.domain;

/** Raised when an operation is attempted while the target aggregate is in an incompatible state. */
public class InvalidStateException extends ZenikiaException {

    public InvalidStateException(String message) {
        super(message);
    }
}
