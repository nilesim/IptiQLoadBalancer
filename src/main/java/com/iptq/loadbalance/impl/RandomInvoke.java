package com.iptq.loadbalance.impl;

import com.iptq.loadbalance.LoadBalancer;

public class RandomInvoke extends LoadBalancer {

    @Override
    public String get() {
        int index = this.random.nextInt(this.instances.size());
        while (this.excludedInstances.contains(this.instances.get(index))) {
            index = this.random.nextInt(this.instances.size());
        }
        return this.instances.get(index);
    }
}
