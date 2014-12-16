package edu.nccu.plsm.osproject.web.api;


import java.util.concurrent.TimeUnit;

public interface Monitorable {

    int getCapacity();

    int remainingCapacity();

    int getMaxTakeTime(TimeUnit unit);

    int getMinTakeTime(TimeUnit unit);

    int getMaxPutTime(TimeUnit unit);

    int getMinPutTime(TimeUnit unit);

    boolean getPutLockState();

    boolean getTakeLockState();

}
