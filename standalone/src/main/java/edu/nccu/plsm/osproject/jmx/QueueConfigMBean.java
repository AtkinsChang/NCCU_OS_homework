package edu.nccu.plsm.osproject.jmx;

public interface QueueConfigMBean {

    public int getCapacity();

    public void setCapacity(int size);

    public int getMaxTakeTime();

    public int getMinTakeTime();

    public void setTakeTimeRage(int min, int max);

    public int getMaxPutTime();

    public int getMinPutTime();

    public void setPutTimeRange(int min, int max);

    public int getSize();

}
