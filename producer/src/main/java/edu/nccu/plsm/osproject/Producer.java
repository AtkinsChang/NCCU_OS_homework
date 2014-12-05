package edu.nccu.plsm.osproject;

import edu.nccu.plsm.osproject.api.ProducerMonitor;
import edu.nccu.plsm.osproject.queue.TimeRange;
import edu.nccu.plsm.osproject.queue.api.ProducerBuffer;
import edu.nccu.plsm.osproject.task.SimpleTask;
import edu.nccu.plsm.osproject.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Producer implements ProducerMonitor, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    private final String name;
    private final ProducerBuffer<Task> producerBuffer;
    private final AtomicLong count;
    private AtomicBoolean isRunning;
    private TimeRange productionTimeRange;
    private TimeRange taskExecutionTimeRange;

    public Producer(String name, ProducerBuffer<Task> producerBuffer) {
        this.name = name;
        this.producerBuffer = producerBuffer;
        this.count = new AtomicLong(0L);
        this.isRunning = new AtomicBoolean(Boolean.FALSE);
        this.productionTimeRange = new TimeRange(5000, 5000);
        this.taskExecutionTimeRange = new TimeRange(5000, 5000);
    }

    public String getName() {
        return name;
    }

    public long getTaskCreationCount() {
        return this.count.get();
    }

    public int getMaxProductionTime() {
        return this.productionTimeRange.getMax();
    }

    public int getMinProductionTime() {
        return this.productionTimeRange.getMin();
    }

    public void setProductionTimeRange(int min, int max) {
        this.productionTimeRange = new TimeRange(min, max);
    }

    public int getMaxTaskExecutionTime() {
        return this.taskExecutionTimeRange.getMax();
    }

    public int getMinTaskExecutionTime() {
        return this.taskExecutionTimeRange.getMin();
    }

    public void setTaskExecutionTimeRange(int min, int max) {
    this.taskExecutionTimeRange = new TimeRange(min, max);
    }


    public void shutdownGracefully() {
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
                Task task = new SimpleTask(this.name, taskExecutionTimeRange.random());
                LOGGER.info("Creating task...");
                count.getAndIncrement();
                Thread.sleep(productionTimeRange.random());
                LOGGER.info("Task creation done, putting to producer buffer");
                producerBuffer.put(task);
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Forced stop, abandon task");
        } finally {
            LOGGER.info("Shutdown complete");
        }
    }

}
