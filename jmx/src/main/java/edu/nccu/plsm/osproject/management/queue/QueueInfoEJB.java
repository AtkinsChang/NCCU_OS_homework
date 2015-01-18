package edu.nccu.plsm.osproject.management.queue;

import edu.nccu.plsm.osproject.queue.OSProjectQueue;
import edu.nccu.plsm.osproject.task.api.Task;

import javax.ejb.Local;

@Local
public interface QueueInfoEJB extends QueueInfoMXBean {

    void setQueue(OSProjectQueue<Task> queue);

}
