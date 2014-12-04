package edu.nccu.plsm.osproject.jmx;

public interface ProducerInfoMBean {

    String getName();

    long getJobCreationCount();

    int getMaxCreationTime();

    int getMinCreationTime();

    void setCreationTimeRange(int max, int min);

    int getMaxTaskProcessTime();

    int getMinTaskProcessTime();

    void setTaskProcessTimeRange(int max, int min);

    void shutdown(boolean interrupt);

}
