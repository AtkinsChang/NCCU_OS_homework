package edu.nccu.plsm.osproject.web;


import com.google.common.collect.ImmutableList;
import edu.nccu.plsm.osproject.Consumer;
import edu.nccu.plsm.osproject.Producer;
import edu.nccu.plsm.osproject.api.ProducerMonitor;
import edu.nccu.plsm.osproject.task.api.Task;
import edu.nccu.plsm.osproject.web.info.ConsumerExecutingInfo;
import edu.nccu.plsm.osproject.web.info.ConsumerJson;
import edu.nccu.plsm.osproject.web.info.ProducerExecutingInfo;
import edu.nccu.plsm.osproject.web.info.ProducerJson;
import edu.nccu.plsm.osproject.web.info.QueueJson;
import edu.nccu.plsm.osproject.web.info.TaskJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Lock(LockType.READ)
@Singleton(name = "ejb/osporject/main")
public class OSProjectImpl implements OSProjectBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSProjectImpl.class);

    @Resource
    private ManagedExecutorService managedExecutorService;
    @Resource(lookup = "concurrent/OSProject/producer")
    private ManagedExecutorService producerPool;
    @Resource(lookup = "concurrent/OSProject/consumer")
    private ManagedExecutorService consumerPool;
    /*
        @EJB(name = "ejb/Control")
        private ControlEJB control;
        @EJB(name = "ejb/QueueInfo")
        private QueueInfoEJB queueInfo;
        */
    private OSProjectQueue<Task> queue;
    private Map<String, ConsumerExecutingInfo> consumers;
    private Map<String, ProducerExecutingInfo> producers;

    @PostConstruct
    private void init() {
        LOGGER.info("Initializing...");
        queue = new OSProjectQueue<>(Integer.MAX_VALUE, managedExecutorService);
        consumers = new HashMap<>();
        producers = new HashMap<>();
        queue.setCapacity(100);
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Destroying...");
        for (ConsumerExecutingInfo c : consumers.values()) {
            c.getFuture().cancel(true);
        }
        for (ProducerExecutingInfo p : producers.values()) {
            p.getFuture().cancel(true);
        }
        queue.releasePutLock();
        queue.releaseTakeLock();
        LOGGER.info("Shutdown complete");
    }

    public QueueJson getQueueJson() {
        return new QueueJson(this.queue);
    }

    public ImmutableList<TaskJson> getTaskJson() {
        ImmutableList.Builder<TaskJson> builder = ImmutableList.builder();
        for (Task t : queue) {
            builder.add(new TaskJson(t));
        }
        return builder.build();
    }

    @Override
    public ImmutableList<ConsumerJson> getConsumerJson() {
        ImmutableList.Builder<ConsumerJson> builder = ImmutableList.builder();
        for (ConsumerExecutingInfo c : consumers.values()) {
            builder.add(new ConsumerJson(c.getConsumerMonitor()));
        }
        return builder.build();
    }

    @Override
    public ImmutableList<ProducerJson> getProducerJson() {
        ImmutableList.Builder<ProducerJson> builder = ImmutableList.builder();
        for (ProducerExecutingInfo p : producers.values()) {
            builder.add(new ProducerJson(p.getProducerMonitor()));
        }
        return builder.build();
    }

    @Override
    @Lock(LockType.WRITE)
    public void updateConsumer(String name, int efficiency) {
        ConsumerExecutingInfo c = consumers.get(name);
        if (c == null) {
            LOGGER.info("Creating new consumer {}", name);
            Consumer consumer = new Consumer(name, queue);
            consumer.setEfficiency(efficiency);
            Future<?> future = this.consumerPool.submit(consumer);
            consumers.put(name, new ConsumerExecutingInfo(consumer, future));
        } else {
            LOGGER.info("Altering consumer {}", name);
            c.getConsumerMonitor().setEfficiency(efficiency);
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void shutdownConsumer(String name, boolean mayInterruptIfRunning) {
        ConsumerExecutingInfo c = consumers.remove(name);
        if (c != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("{} shutdown consumer {}", mayInterruptIfRunning ? "force" : "gracefully", name);
            }
            c.getConsumerMonitor().shutdownGracefully();
            c.getFuture().cancel(mayInterruptIfRunning);
        } else {
            LOGGER.info("Consumer {} not found", name);
            throw new EJBException(name + " not found");
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void updateProducer(String name, int pMin, int pMax, int tMin, int tMax) {
        ProducerExecutingInfo p = producers.get(name);
        if (p == null) {
            LOGGER.info("Creating new producer {}", name);
            Producer producer = new Producer(name, queue);
            producer.setProductionTimeRange(pMin, pMax);
            producer.setTaskExecutionTimeRange(tMin, tMax);
            Future<?> future = this.producerPool.submit(producer);
            producers.put(name, new ProducerExecutingInfo(producer, future));
        } else {
            LOGGER.info("Altering producer {}", name);
            ProducerMonitor pm = p.getProducerMonitor();
            pm.setProductionTimeRange(pMin, pMax);
            pm.setTaskExecutionTimeRange(tMin, tMax);
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void shutdownProducer(String name, boolean mayInterruptIfRunning) {
        ProducerExecutingInfo p = producers.remove(name);
        if (p != null) {
            LOGGER.info("{} shutdown producer {}", mayInterruptIfRunning ? "force" : "gracefully", name);
            p.getProducerMonitor().shutdownGracefully();
            p.getFuture().cancel(mayInterruptIfRunning);
        } else {
            LOGGER.info("Producer {} not found", name);
            throw new EJBException(name + " not found");
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void updateQueue(int capacity, int pMin, int pMax, int tMin, int tMax) {
        queue.setCapacity(capacity);
        queue.setPutTime(pMin, pMax, TimeUnit.MILLISECONDS);
        queue.setTakeTime(tMin, tMax, TimeUnit.MILLISECONDS);
    }

    @Override
    @Lock(LockType.WRITE)
    public void lockPut() {
        queue.acquirePutLock();
    }

    @Override
    @Lock(LockType.WRITE)
    public void unlockPut() {
        queue.releasePutLock();
    }

    @Override
    @Lock(LockType.WRITE)
    public void lockTake() {
        queue.acquireTakeLock();
    }

    @Override
    @Lock(LockType.WRITE)
    public void unlockTake() {
        queue.releaseTakeLock();
    }

}
