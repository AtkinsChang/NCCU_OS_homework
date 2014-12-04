package edu.nccu.plsm.osproject.jmx;

import edu.nccu.plsm.osproject.queue.Configurable;

import java.util.concurrent.TimeUnit;

public class QueueConfig implements QueueConfigMBean {

    private final Configurable queue;
    private final TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    public QueueConfig(Configurable queue) {
        super();
        this.queue = queue;
    }

    @Override
    public int getCapacity() {
        return queue.getCapacity();
    }

    @Override
    public void setCapacity(int size) {
        queue.setCapacity(size);
    }

    @Override
    public int getMaxTakeTime() {
        return queue.getMaxTakeTime(timeUnit);
    }

    @Override
    public int getMinTakeTime() {
        return queue.getMinTakeTime(timeUnit);
    }

    @Override
    public void setTakeTimeRage(int min, int max) {
        queue.setTakeTime(min, max, timeUnit);
    }

    @Override
    public int getMaxPutTime() {
        return queue.getMaxPutTime(timeUnit);
    }

    @Override
    public int getMinPutTime() {
        return queue.getMinPutTime(timeUnit);
    }

    @Override
    public void setPutTimeRange(int min, int max) {
        queue.setPutTime(min, max, timeUnit);
    }

    public int getSize() {
        return queue.getCapacity() - queue.remainingCapacity();
    }

}
