package edu.nccu.plsm.osproject.api;

public interface ConsumerMonitor {

    String getName();

    int getState();

    long getTaskExecutionCount();

    void shutdownGracefully();

    int getEfficiency();

    void setEfficiency(int efficiency);

}
