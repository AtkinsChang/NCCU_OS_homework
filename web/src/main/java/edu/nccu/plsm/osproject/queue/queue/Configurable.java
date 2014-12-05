package edu.nccu.plsm.osproject.queue.queue;

import java.util.concurrent.TimeUnit;

public interface Configurable {

    int getCapacity();

    void setCapacity(int size);

    int getRemainingCapacity();

    int getUsedCapacity();

    void setTakeTime(int min, int max, TimeUnit unit);

    void setPutTime(int min, int max, TimeUnit unit);

    int getMaxTakeTime(TimeUnit unit);

    int getMinTakeTime(TimeUnit unit);

    int getMaxPutTime(TimeUnit unit);

    int getMinPutTime(TimeUnit unit);

}
