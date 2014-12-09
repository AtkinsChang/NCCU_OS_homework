package edu.nccu.plsm.osproject.queue.message.entity;

import edu.nccu.plsm.osproject.queue.OSProjectQueue;

import java.util.concurrent.TimeUnit;

public class QueueJson {

    private final int totalCapacity;
    private final int usedCapacity;
    private final int maxTakeTime;
    private final int minTakeTime;
    private final int maxPutTime;
    private final int minPutTime;
    private final boolean takeLockState;
    private final boolean putLockState;


    public QueueJson(OSProjectQueue queue) {
        this.totalCapacity = queue.getCapacity();
        this.usedCapacity = queue.size();
        this.maxPutTime = queue.getMaxPutTime(TimeUnit.MILLISECONDS);
        this.minPutTime = queue.getMinPutTime(TimeUnit.MILLISECONDS);
        this.maxTakeTime = queue.getMaxTakeTime(TimeUnit.MILLISECONDS);
        this.minTakeTime = queue.getMinTakeTime(TimeUnit.MILLISECONDS);
        this.takeLockState = queue.getTakeLockState();
        this.putLockState = queue.getPutLockState();
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getUsedCapacity() {
        return usedCapacity;
    }

    public int getMaxTakeTime() {
        return maxTakeTime;
    }

    public int getMinTakeTime() {
        return minTakeTime;
    }

    public int getMaxPutTime() {
        return maxPutTime;
    }

    public int getMinPutTime() {
        return minPutTime;
    }

    public boolean isTakeLockState() {
        return takeLockState;
    }

    public boolean isPutLockState() {
        return putLockState;
    }

}
