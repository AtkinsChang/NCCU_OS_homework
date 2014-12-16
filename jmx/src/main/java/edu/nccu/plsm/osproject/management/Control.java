package edu.nccu.plsm.osproject.management;

import edu.nccu.plsm.osproject.Consumer;
import edu.nccu.plsm.osproject.Producer;
import edu.nccu.plsm.osproject.management.consumer.ConsumerInfo;
import edu.nccu.plsm.osproject.management.consumer.ConsumerInfoMBean;
import edu.nccu.plsm.osproject.management.producer.ProducerInfo;
import edu.nccu.plsm.osproject.management.producer.ProducerInfoMBean;
import edu.nccu.plsm.osproject.task.api.Task;
import edu.nccu.plsm.osproject.web.OSProjectQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;

@Stateless(name = "ejb/Control")
public class Control implements ControlEJB {

    private static final Logger LOGGER = LoggerFactory.getLogger(Control.class);
    private static final MBeanServer M_BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
    private static final ObjectName NAME;

    static {
        ObjectName name = null;
        try {
            name = new ObjectName("OS.Project:name=Control");
        } catch (MalformedObjectNameException ignored) {
        }
        NAME = name;
    }

    private OSProjectQueue<Task> queue;
    private ExecutorService producerPool;
    private ExecutorService consumerPool;

    @Resource
    private ManagedExecutorService es;

    public Control() {
        super();
    }

    @PostConstruct
    private void initBean() {
        LOGGER.debug("Initializing {}...", getClass().getSimpleName());
        this.producerPool = es;
        this.consumerPool = es;
        register();
    }

    @PreDestroy
    private void onShutdown() {
        LOGGER.debug("Shutting down {}...", getClass().getSimpleName());
        try {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            platformMBeanServer.unregisterMBean(NAME);
        } catch (InstanceNotFoundException | MBeanRegistrationException e) {
            LOGGER.info("Shutdown error", e);
        }
    }

    private void register() {
        LOGGER.debug("Registering {}...", getClass().getSimpleName());
        try {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            platformMBeanServer.registerMBean(this, NAME);
        } catch (NotCompliantMBeanException
                | InstanceAlreadyExistsException
                | MBeanRegistrationException e) {
            LOGGER.error("Register error", e);
        }
    }

    public void init(ExecutorService producerPool, ExecutorService consumerPool) {
        this.producerPool = producerPool;
        this.consumerPool = consumerPool;
        register();
    }

    public void setQueue(OSProjectQueue<Task> queue) {
        this.queue = queue;
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
        LOGGER.info("Add producer: {}", name);
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
        } catch (Exception e) {
            LOGGER.error("Internal Error", e);
        }
    }

    @Override
    public void addNewConsumer(String name) {
        LOGGER.info("Add consumer {}", name);
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
        } catch (Exception e) {
            LOGGER.error("Internal Error", e);
        }
    }

}
