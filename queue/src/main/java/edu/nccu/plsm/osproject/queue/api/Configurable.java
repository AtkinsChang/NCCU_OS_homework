package edu.nccu.plsm.osproject.queue.api;

import java.util.concurrent.TimeUnit;

public interface Configurable {

    int getCapacity();

    void setCapacity(int size);

    int remainingCapacity();

    int size();

    void setTakeTime(int min, int max, TimeUnit unit);

    void setPutTime(int min, int max, TimeUnit unit);

    int getMaxTakeTime(TimeUnit unit);

    int getMinTakeTime(TimeUnit unit);

    int getMaxPutTime(TimeUnit unit);

    int getMinPutTime(TimeUnit unit);

}
