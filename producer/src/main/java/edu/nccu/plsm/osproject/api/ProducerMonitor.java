package edu.nccu.plsm.osproject.api;

public interface ProducerMonitor {

    String getName();

    int getState();

    void shutdownGracefully();

    long getTaskCreationCount();

    int getMaxProductionTime();

    int getMinProductionTime();

    void setProductionTimeRange(int min, int max);

    int getMaxTaskExecutionTime();

    int getMinTaskExecutionTime();

    void setTaskExecutionTimeRange(int min, int max);

}
