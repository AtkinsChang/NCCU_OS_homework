package edu.nccu.plsm.osproject;

import edu.nccu.plsm.osproject.queue.ConfigurableQueue;
import edu.nccu.plsm.osproject.task.Task;
import edu.nccu.plsm.osproject.task.TaskImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Producer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    private final String name;
    private final BlockingQueue<Task> queue;
    private final AtomicLong count;
    private AtomicBoolean isRunning;
    private Range producingTimeRange;
    private Range taskExecuteTimeRange;

    public Producer(String name, ConfigurableQueue<Task> queue) {
        this.name = name;
        this.queue = queue;
        this.count = new AtomicLong(0L);
        this.isRunning = new AtomicBoolean(Boolean.FALSE);
        this.producingTimeRange = new Range(5000, 5000);
        this.taskExecuteTimeRange = new Range(5000, 5000);
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return this.count.get();
    }

    public Range getProducingTimeRange() {
        return producingTimeRange;
    }

    public void setProducingTimeRange(Range producingTimeRange) {
        this.producingTimeRange = producingTimeRange;
    }

    public Range getTaskExecuteTimeRange() {
        return taskExecuteTimeRange;
    }

    public void setTaskExecuteTimeRange(Range taskExecuteTimeRange) {
        this.taskExecuteTimeRange = taskExecuteTimeRange;
    }


    public void shutdown() {
        this.isRunning.getAndSet(Boolean.FALSE);
    }

    @Override
    public void run() {
        //this.runningThread = Thread.currentThread();
        //this.runningThread.setName("producer-" + name);
        MDC.put("name", name);
        isRunning.set(Boolean.TRUE);
        try {
            while (isRunning.get()) {
                Task task = new TaskImpl(this.name, taskExecuteTimeRange.rand());
                LOGGER.info("Creating task...");
                count.getAndIncrement();
                Thread.sleep(producingTimeRange.rand());
                LOGGER.info("Creating done, putting to queue");
                queue.put(task);
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Forced stop, abandon created task");
        } finally {
            LOGGER.info("Shutdown complete");
        }
    }

}
