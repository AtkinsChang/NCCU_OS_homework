package edu.nccu.plsm.osproject.queue;

import edu.nccu.plsm.osproject.queue.jmx.Control;
import edu.nccu.plsm.osproject.queue.jmx.ControlMBean;
import edu.nccu.plsm.osproject.queue.jmx.QueueConfig;
import edu.nccu.plsm.osproject.queue.jmx.QueueConfigMBean;
import edu.nccu.plsm.osproject.queue.queue.ConfigurableQueue;
import edu.nccu.plsm.osproject.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@Singleton
public class OSProjectBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSProjectBean.class);
    @Resource
    private ManagedExecutorService managedExecutorService;
    private ConfigurableQueue<Task> queue;

    @PostConstruct
    private void init() {
        queue = new ConfigurableQueue<>(managedExecutorService);
        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName queueMBeanName = new ObjectName("OSProjectBean:name=Queue");
            ObjectName controlMBeanName = new ObjectName("OSProjectBean:name=Control");

            QueueConfigMBean queueBean = new QueueConfig(queue);
            ControlMBean controlMBean = new Control(queue, managedExecutorService);

            platformMBeanServer.registerMBean(queueBean, queueMBeanName);
            platformMBeanServer.registerMBean(controlMBean, controlMBeanName);
        } catch (MalformedObjectNameException
                |NotCompliantMBeanException
                |InstanceAlreadyExistsException
                |MBeanRegistrationException e) {
            LOGGER.error("init error", e);
        }
    }
}
