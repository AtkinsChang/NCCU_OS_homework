package edu.nccu.plsm.osproject.management;

import edu.nccu.plsm.osproject.queue.OSProjectQueue;
import edu.nccu.plsm.osproject.task.api.Task;

import javax.ejb.Local;

@Local
public interface ControlEJB extends ControlMBean {

    void setQueue(OSProjectQueue<Task> queue);

}
