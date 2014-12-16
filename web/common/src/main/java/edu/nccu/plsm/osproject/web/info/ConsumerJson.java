package edu.nccu.plsm.osproject.web.info;

import edu.nccu.plsm.osproject.api.ConsumerMonitor;

public class ConsumerJson {

    final private String name;
    final private int state;
    final private int efficiency;
    final private long taskExecutionCount;

    public ConsumerJson(ConsumerMonitor consumerMonitor) {
        this.name = consumerMonitor.getName();
        this.state = consumerMonitor.getState();
        this.efficiency = consumerMonitor.getEfficiency();
        this.taskExecutionCount = consumerMonitor.getTaskExecutionCount();
    }

    public String getName() {
        return name;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public long getTaskExecutionCount() {
        return taskExecutionCount;
    }

    public int getState() {
        return state;
    }
}