package edu.nccu.plsm.osproject.jmx;

public interface ConsumerInfoMBean {

    String getName();

    long getJobCompleteCount();

    int getEfficiency();

    void setEfficiency(int efficiency);

    void shutdown(boolean interrupt);

}