package edu.nccu.plsm.osproject.management.queue;

import edu.nccu.plsm.osproject.management.task.TaskInfoMBean;

import java.util.Set;

public interface QueueInfoMXBean {

    Set<TaskInfoMBean> getQueue();

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

    boolean getPutLockState();

    boolean getTakeLockState();

}
