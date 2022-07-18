package com.iptq.loadbalance.impl;

import com.iptq.loadbalance.LoadBalancer;
import java.util.Random;

public class RandomInvoke extends LoadBalancer {

    @Override
    public String get() {
        int index = new Random().nextInt(this.instances.size());
        while (this.excludedInstances.contains(this.instances.get(index))) {
            index = new Random().nextInt(this.instances.size());
        }
        return this.instances.get(index);
    }
}
