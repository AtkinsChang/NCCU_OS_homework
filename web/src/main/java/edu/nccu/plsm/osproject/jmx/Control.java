package edu.nccu.plsm.osproject.jmx;

import edu.nccu.plsm.osproject.Consumer;
import edu.nccu.plsm.osproject.Producer;
import edu.nccu.plsm.osproject.queue.ConfigurableQueue;
import edu.nccu.plsm.osproject.task.Task;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;


public class Control implements ControlMBean {

    private static final MBeanServer M_BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    private final ConfigurableQueue<Task> queue;
    private final ExecutorService es;

    public Control(ConfigurableQueue<Task> queue, ExecutorService es) {
        this.queue = queue;
        this.es = es;
    }


    @Override
    public void acquirePutLock() {
        queue.acquirePutLock();
    }

    @Override
    public void releasePutLock() {
        queue.releasePutLock();
    }

    @Override
    public void acquireTakeLock() {
        queue.acquireTakeLock();
    }

    @Override
    public void releaseTakeLock() {
        queue.releaseTakeLock();
    }

    @Override
    public void addNewProducer(String name) {
        Producer p = new Producer(name, queue);
        ProducerInfoMBean pm = new ProducerInfo(p);
        try {
            ObjectName pmName = new ObjectName("App:service=producer,name=" + name);
            M_BEAN_SERVER.registerMBean(pm, pmName);
            ((ProducerInfo) pm).setFuture(es.submit(p));
        } catch (MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        } catch (InstanceAlreadyExistsException e) {
            throw new IllegalArgumentException("Duplicate name", e);
        }
    }

    @Override
    public void addNewConsumer(String name) {
        Consumer c = new Consumer(name, queue);
        ConsumerInfoMBean cm = new ConsumerInfo(c);
        try {
            ObjectName cmName = new ObjectName("App:service=consumer,name=" + name);
            M_BEAN_SERVER.registerMBean(cm, cmName);
            ((ConsumerInfo) cm).setFuture(es.submit(c));
        } catch (MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        } catch (InstanceAlreadyExistsException e) {
            throw new IllegalArgumentException("Duplicate name", e);
        }
    }
}
