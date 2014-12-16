package edu.nccu.plsm.osproject.management;

import edu.nccu.plsm.osproject.web.api.Lockable;

import javax.ejb.Local;

@Local
public interface ControlMBean extends Lockable {

    public void addNewProducer(String name);

    public void addNewConsumer(String name);

}
