package com.zenika.zenikia.shared.domain;

/** Raised when a referenced resource (session, question, profile...) does not exist. */
public class ResourceNotFoundException extends ZenikiaException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resourceName, String id) {
        return new ResourceNotFoundException("%s introuvable : %s".formatted(resourceName, id));
    }
}
