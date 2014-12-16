package edu.nccu.plsm.osproject.web.info;

import edu.nccu.plsm.osproject.api.ProducerMonitor;

public class ProducerJson {

    final private String name;
    final private int state;
    final private long taskCreationCount;
    final private int maxProductionTime;
    final private int minProductionTime;
    final private int maxTaskExecutionTim;
    final private int minTaskExecutionTim;


    public ProducerJson(ProducerMonitor producerMonitor) {
        this.name = producerMonitor.getName();
        this.state = producerMonitor.getState();
        this.taskCreationCount = producerMonitor.getTaskCreationCount();
        this.maxProductionTime = producerMonitor.getMaxProductionTime();
        this.minProductionTime = producerMonitor.getMinProductionTime();
        this.maxTaskExecutionTim = producerMonitor.getMaxTaskExecutionTime();
        this.minTaskExecutionTim = producerMonitor.getMinTaskExecutionTime();
    }

    public String getName() {
        return name;
    }

    public int getState() {
        return state;
    }

    public long getTaskCreationCount() {
        return taskCreationCount;
    }

    public int getMaxProductionTime() {
        return maxProductionTime;
    }

    public int getMinProductionTime() {
        return minProductionTime;
    }

    public int getMaxTaskExecutionTime() {
        return maxTaskExecutionTim;
    }

    public int getMinTaskExecutionTime() {
        return minTaskExecutionTim;
    }

}
