package edu.nccu.plsm.osproject.queue;

import java.util.concurrent.TimeUnit;

public interface Configurable {

    public int getCapacity();

    public void setCapacity(int size);

    void setTakeTime(int min, int max, TimeUnit unit);

    void setPutTime(int min, int max, TimeUnit unit);

    public int getMaxTakeTime(TimeUnit unit);

    public int getMinTakeTime(TimeUnit unit);

    public int getMaxPutTime(TimeUnit unit);

    public int getMinPutTime(TimeUnit unit);

    public int remainingCapacity();

}
