package com.iptq.service;

import java.util.Random;
import java.util.UUID;

public class Provider {

    private UUID providerId;
    private Random random;

    void Provider() {
        this.providerId = UUID.randomUUID();
        random = new Random();
    }

    public String getInstance() {
        return this.providerId.toString();
    }

    public Boolean check() {
        return random.nextBoolean();
    }
}
