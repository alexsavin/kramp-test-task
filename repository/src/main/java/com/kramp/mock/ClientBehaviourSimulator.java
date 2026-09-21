package com.kramp.mock;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.Random;

public abstract class ClientBehaviourSimulator {

    private int latency = 0;
    private int reliability = 1000;
    private final boolean[] reliabilityArr = new boolean[1000];

    protected ClientBehaviourSimulator(int latency, int reliability) {
        this.latency = latency;
        this.reliability = reliability;
    }

    @PostConstruct
    public void init() {
        Arrays.fill(reliabilityArr, false);

        for (int i = 0; i < (Math.min(reliability, 1000)); i++) {
            reliabilityArr[i] = true;
        }
    }

    public void simulateLatency() {
        try {
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean serviceAvailable() {
        Random r = new Random();
        return reliabilityArr[r.ints(0, 1000).findFirst().getAsInt()];
    }
}
