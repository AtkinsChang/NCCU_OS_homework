package edu.nccu.plsm.osproject.web.info;

import edu.nccu.plsm.osproject.task.api.Task;

public class TaskJson {

    private final String creatorName;
    private final int timeNeeded;
    private final int turnaroundTime;

    public TaskJson(Task task) {
        this.creatorName = task.getCreatorName();
        this.timeNeeded = task.getTimeNeeded();
        this.turnaroundTime = task.getTurnaroundTime();
    }

    public String getCreatorName() {
        return creatorName;
    }

    public int getTimeNeeded() {
        return timeNeeded;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

}
