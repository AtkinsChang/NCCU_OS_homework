package edu.nccu.plsm.osproject.management;

import edu.nccu.plsm.osproject.queue.api.Lockable;

public interface ControlMBean extends Lockable{

    public void addNewProducer(String name);

    public void addNewConsumer(String name);

}
