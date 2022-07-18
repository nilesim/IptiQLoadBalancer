package com.iptq.loadbalance.impl;

import com.iptq.loadbalance.LoadBalancer;

public class RoundRobinInvoke extends LoadBalancer {

    private int index;

    public RoundRobinInvoke() {
        super();
        this.index = -1;
    }

    @Override
    public String get() {
        index++;
        while (this.excludedInstances.contains(this.instances.get(index))) {
            index++;
        }
        return this.instances.get(index);
    }
}
