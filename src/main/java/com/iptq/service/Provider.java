package com.iptq.service;

import java.util.Random;
import java.util.UUID;

public class Provider {

    private UUID providerId;

    void Provider() {
        this.providerId = UUID.randomUUID();
    }

    public String getInstance() {
        return this.providerId.toString();
    }

    public Boolean check() {
        return new Random().nextBoolean();
    }
}
