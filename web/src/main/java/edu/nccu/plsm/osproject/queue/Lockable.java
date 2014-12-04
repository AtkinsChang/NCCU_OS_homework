package edu.nccu.plsm.osproject.queue;

import javax.ejb.Local;

public interface Lockable {

    public void acquirePutLock();

    public void releasePutLock();

    public void acquireTakeLock();

    public void releaseTakeLock();

}
