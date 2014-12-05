package edu.nccu.plsm.osproject.management.consumer;

import edu.nccu.plsm.osproject.api.ConsumerMonitor;

public interface ConsumerInfoMBean {

    String getName();

    long getTaskExecutionCount();

    void shutdown(boolean mayInterruptIfRunning);

    int getEfficiency();

    void setEfficiency(int efficiency);

}