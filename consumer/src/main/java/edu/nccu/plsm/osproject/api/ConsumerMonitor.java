package edu.nccu.plsm.osproject.api;

public interface ConsumerMonitor {

    String getName();

    long getTaskExecutionCount();

    void shutdownGracefully();

    int getEfficiency();

    void setEfficiency(int efficiency);

}
