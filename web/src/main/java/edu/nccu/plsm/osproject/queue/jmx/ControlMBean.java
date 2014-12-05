package edu.nccu.plsm.osproject.queue.jmx;

public interface ControlMBean {

    public void acquirePutLock();

    public void releasePutLock();

    public void acquireTakeLock();

    public void releaseTakeLock();

    public void addNewProducer(String name);

    public void addNewConsumer(String name);
}
