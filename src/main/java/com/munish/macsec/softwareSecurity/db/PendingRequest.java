package com.munish.macsec.softwareSecurity.db;

public class PendingRequest {
    String email;

    String requestedOn;
    public PendingRequest(String email, String requestedOn) {
        this.email=email;

        this.requestedOn =requestedOn;
    }

    public String getEmail() {
        return email;
    }

    public String getRequestedOn() {
        return requestedOn;
    }
}
