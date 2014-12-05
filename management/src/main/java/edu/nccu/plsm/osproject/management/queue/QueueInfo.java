package edu.nccu.plsm.osproject.management.queue;

import edu.nccu.plsm.osproject.queue.api.Configurable;

import java.util.concurrent.TimeUnit;

public class QueueInfo implements QueueInfoMBean {

    private final Configurable configurable;
    private final TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    public QueueInfo(Configurable configurable) {
        super();
        this.configurable = configurable;
    }

    @Override
    public int getCapacity() {
        return configurable.getCapacity();
    }

    @Override
    public void setCapacity(int size) {
        configurable.setCapacity(size);
    }

    @Override
    public int getRemainingCapacity() {
        return configurable.remainingCapacity();
    }

    @Override
    public int getMaxTakeTime() {
        return configurable.getMaxTakeTime(timeUnit);
    }

    @Override
    public int getMinTakeTime() {
        return configurable.getMinTakeTime(timeUnit);
    }

    @Override
    public void setTakeTime(int min, int max) {
        configurable.setTakeTime(min, max, timeUnit);
    }

    @Override
    public int getMaxPutTime() {
        return configurable.getMaxPutTime(timeUnit);
    }

    @Override
    public int getMinPutTime() {
        return configurable.getMinPutTime(timeUnit);
    }

    @Override
    public void setPutTime(int min, int max) {
        configurable.setPutTime(min, max, timeUnit);
    }

    @Override
    public int getUsedCapacity() {
        return configurable.size();
    }

}
