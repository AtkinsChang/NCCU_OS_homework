package edu.nccu.plsm.osproject.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class TaskImpl implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskImpl.class);

    private final String creatorName;
    private int timeNeeded;
    private int turnaroundTime;

    public TaskImpl(String creatorName, int timeNeeded) {
        super();
        this.creatorName = creatorName;
        this.timeNeeded = timeNeeded;
        this.turnaroundTime = 0;
    }

    public String getCreatorName() {
        return creatorName;
    }

    @Override
    public void doTask(int efficiency) {
        LOGGER.trace("Task {} Start", this.toString());
        long timeStart = System.nanoTime();
        try {
            Thread.sleep(timeNeeded * 10 / efficiency);
        } catch (InterruptedException e) {
            long timeInterrupted = System.nanoTime();
            LOGGER.info("Task {} interrupt", this.toString());
            this.timeNeeded = (int) TimeUnit.NANOSECONDS.toMillis(timeStart - timeInterrupted);
            this.turnaroundTime++;
        }
    }

}