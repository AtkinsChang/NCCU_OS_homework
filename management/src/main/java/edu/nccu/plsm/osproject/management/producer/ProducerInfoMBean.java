package edu.nccu.plsm.osproject.management.producer;

public interface ProducerInfoMBean {

    String getName();

    long getTaskCreationCount();

    int getMaxProductionTime();

    int getMinProductionTime();

    void setProductionTimeRange(int min, int max);

    int getMaxTaskExecutionTime();

    int getMinTaskExecutionTime();

    void setTaskExecutionTimeRange(int min, int max);

    void shutdown(boolean interrupt);

}
