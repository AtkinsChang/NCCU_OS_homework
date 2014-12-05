package edu.nccu.plsm.osproject.queue;

import edu.nccu.plsm.osproject.queue.queue.ConfigurableQueue;
import edu.nccu.plsm.osproject.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Consumer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private final String name;
    private final BlockingQueue<Task> queue;
    private AtomicBoolean isRunning;
    private AtomicInteger efficiency;
    private AtomicLong count;


    public Consumer(String name, ConfigurableQueue<Task> queue) {
        this.name = name;
        MDC.put("name", name);
        this.queue = queue;
        this.isRunning = new AtomicBoolean(Boolean.FALSE);
        this.efficiency = new AtomicInteger(10);
        this.count = new AtomicLong(0L);
    }

    public String getName() {
        return name;
    }

    public int getEfficiency() {
        return efficiency.get();
    }

    public void setEfficiency(int efficiency) {
        this.efficiency.getAndSet(efficiency);
    }

    public void shutdown() {
        isRunning.getAndSet(Boolean.FALSE);
    }

    public long getCount() {
        return count.get();
    }

    @Override
    public void run() {
        //this.runningThread = Thread.currentThread();
        //this.runningThread.setName("consumer-" + name);
        this.isRunning.getAndSet(Boolean.TRUE);
        MDC.put("name", name);
        Task task = null;
        try {
            while (isRunning.get()) {
                LOGGER.info("Taking task from queue");
                task = queue.take();
                LOGGER.info("Executing task create by {}...", task.getCreatorName());
                count.getAndIncrement();
                task.doTask(efficiency.get());
                LOGGER.trace("Task complete");
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Forced stop, put back task");
            if (null != task) {
                queue.offer(task);
            }
        }
    }

}