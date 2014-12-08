package edu.nccu.plsm.osproject.management.queue;

import com.google.common.collect.ImmutableSet;
import edu.nccu.plsm.osproject.management.task.TaskInfo;
import edu.nccu.plsm.osproject.management.task.TaskInfoMBean;
import edu.nccu.plsm.osproject.task.api.Task;
import edu.nccu.plsm.osproject.queue.OSProjectQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Stateful(name = "ejb/QueueInfo")
public class QueueInfo implements QueueInfoEJB {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueInfo.class);
    private static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private static final ObjectName NAME;

    static {
        ObjectName name = null;
        try {
            name = new ObjectName("OS.Project:service=queue,name=QueueInfo");
        } catch (MalformedObjectNameException e) {
            LOGGER.error("init error", e);
        }
        NAME = name;
    }

    private OSProjectQueue<Task> queue;

    public QueueInfo() {
        super();
    }

    @PostConstruct
    public void initBean() {
        LOGGER.info("Initializing {}...", getClass().getSimpleName());
        init();
    }

    @PreDestroy
    public void shutdown() {
        LOGGER.debug("Shutting down {}...", getClass().getSimpleName());
        try {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            platformMBeanServer.unregisterMBean(NAME);
        } catch (InstanceNotFoundException | MBeanRegistrationException e) {
            LOGGER.info("Shutdown error", e);
        }
    }

    public void init() {
        LOGGER.info("Registering {}...", getClass().getSimpleName());
        try {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            platformMBeanServer.registerMBean(this, NAME);
        } catch (NotCompliantMBeanException
                | InstanceAlreadyExistsException
                | MBeanRegistrationException e) {
            LOGGER.error("register error", e);
        }
    }

    @Override
    public Set<TaskInfoMBean> getQueue() {
        ImmutableSet.Builder<TaskInfoMBean> setBuilder = ImmutableSet.builder();
        for (Task t : queue) {
            setBuilder.add(new TaskInfo(t));
        }
        return setBuilder.build();
    }

    public void setQueue(OSProjectQueue<Task> queue) {
        this.queue = queue;
    }

    @Override
    public int getCapacity() {
        return queue.getCapacity();
    }

    @Override
    public void setCapacity(int size) {
        queue.setCapacity(size);
    }

    @Override
    public int getRemainingCapacity() {
        return queue.remainingCapacity();
    }

    @Override
    public int getMaxTakeTime() {
        return queue.getMaxTakeTime(timeUnit);
    }

    @Override
    public int getMinTakeTime() {
        return queue.getMinTakeTime(timeUnit);
    }

    @Override
    public void setTakeTime(int min, int max) {
        queue.setTakeTime(min, max, timeUnit);
    }

    @Override
    public int getMaxPutTime() {
        return queue.getMaxPutTime(timeUnit);
    }

    @Override
    public int getMinPutTime() {
        return queue.getMinPutTime(timeUnit);
    }

    @Override
    public boolean getPutLockState() {
        return queue.getPutLockState();
    }

    @Override
    public boolean getTakeLockState() {
        return queue.getTakeLockState();
    }

    @Override
    public void setPutTime(int min, int max) {
        queue.setPutTime(min, max, timeUnit);
    }

    @Override
    public int getUsedCapacity() {
        return queue.size();
    }

}
