package edu.nccu.plsm.osproject.management;

import edu.nccu.plsm.osproject.Consumer;
import edu.nccu.plsm.osproject.Producer;
import edu.nccu.plsm.osproject.management.consumer.ConsumerInfo;
import edu.nccu.plsm.osproject.management.consumer.ConsumerInfoMBean;
import edu.nccu.plsm.osproject.management.producer.ProducerInfo;
import edu.nccu.plsm.osproject.management.producer.ProducerInfoMBean;
import edu.nccu.plsm.osproject.queue.OSProjectQueue;
import edu.nccu.plsm.osproject.task.api.Task;
import org.slf4j.Logger;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Control.class);
    private static final MBeanServer M_BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    private final OSProjectQueue<Task> queue;
    private final ExecutorService producerPool;
    private final ExecutorService consumerPool;

    public Control(OSProjectQueue<Task> queue, ExecutorService producerPool, ExecutorService consumerPool) {
        this.queue = queue;
        this.producerPool = producerPool;
        this.consumerPool = consumerPool;
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
        Producer producer = new Producer(name, queue);
        ProducerInfoMBean producerMBean = new ProducerInfo(producer);
        try {
            ObjectName producerMBeanName = new ObjectName("OS.Project:service=producer,name=" + name);
            M_BEAN_SERVER.registerMBean(producerMBean, producerMBeanName);
            ((ProducerInfo) producerMBean).setFuture(producerPool.submit(producer));
        } catch (MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) {
            LOGGER.error("Internal Error", e);
        } catch (InstanceAlreadyExistsException e) {
            LOGGER.warn("Producer name already exist", e);
            throw new IllegalArgumentException("Duplicate name", e);
        }
    }

    @Override
    public void addNewConsumer(String name) {
        Consumer consumer = new Consumer(name, queue);
        ConsumerInfoMBean consumerMBean = new ConsumerInfo(consumer);
        try {
            ObjectName consumerMBeanName = new ObjectName("OS.Project:service=consumer,name=" + name);
            M_BEAN_SERVER.registerMBean(consumerMBean, consumerMBeanName);
            ((ConsumerInfo) consumerMBean).setFuture(consumerPool.submit(consumer));
        } catch (MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) {
            LOGGER.error("Internal Error", e);
        } catch (InstanceAlreadyExistsException e) {
            LOGGER.warn("Consumer name already exist", e);
        }
    }

}
