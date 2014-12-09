package edu.nccu.plsm.osproject.management.task;

import edu.nccu.plsm.osproject.task.api.Task;

public class TaskInfo implements TaskInfoMBean {

    private final Task task;

    public TaskInfo(Task task) {
        this.task = task;
    }

    @Override
    public String getCreatorName() {
        return task.getCreatorName();
    }

    @Override
    public int getTimeNeeded() {
        return task.getTimeNeeded();
    }

    @Override
    public int getTurnaroundTime() {
        return task.getTimeNeeded();
    }

}
