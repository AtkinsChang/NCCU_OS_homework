package edu.nccu.plsm.osproject.task;

import edu.nccu.plsm.osproject.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SimpleTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTask.class);

    private final String creatorName;
    private int timeNeeded;
    private int turnaroundTime;

    public SimpleTask(String creatorName, int timeNeeded) {
        super();
        this.creatorName = creatorName;
        this.timeNeeded = timeNeeded;
        this.turnaroundTime = 0;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public int getTurnaroundTime() {
        return this.turnaroundTime;
    }

    @Override
    public void doTask(int efficiency) throws TaskNotCompleteException {
        LOGGER.trace("Task {} Start", this.toString());
        long timeStart = System.nanoTime();
        try {
            Thread.sleep(timeNeeded * 10 / efficiency);
        } catch (InterruptedException e) {
            long timeInterrupted = System.nanoTime();
            LOGGER.info("Task {} interrupt while executing", this.toString(), e);
            this.timeNeeded = (int) TimeUnit.NANOSECONDS.toMillis(timeStart - timeInterrupted);
            this.turnaroundTime++;
            throw new TaskNotCompleteException("interrupt", e);
        }
    }

}