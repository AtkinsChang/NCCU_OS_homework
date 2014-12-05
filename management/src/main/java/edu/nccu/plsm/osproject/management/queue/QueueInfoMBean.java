package edu.nccu.plsm.osproject.management.queue;

import java.util.concurrent.TimeUnit;

public interface QueueInfoMBean {

    int getCapacity();

    void setCapacity(int capacity);

    int getRemainingCapacity();

    int getUsedCapacity();

    void setTakeTime(int min, int max);

    void setPutTime(int min, int max);

    int getMaxTakeTime();

    int getMinTakeTime();

    int getMaxPutTime();

    int getMinPutTime();

}
