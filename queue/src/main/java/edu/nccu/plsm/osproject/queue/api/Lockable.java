package edu.nccu.plsm.osproject.queue.api;

public interface Lockable {

    public void acquirePutLock();

    public void releasePutLock();

    public void acquireTakeLock();

    public void releaseTakeLock();

}
