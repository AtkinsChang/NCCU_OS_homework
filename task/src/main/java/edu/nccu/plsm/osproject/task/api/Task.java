package edu.nccu.plsm.osproject.task.api;

import edu.nccu.plsm.osproject.task.TaskNotCompleteException;

public interface Task {

    String getCreatorName();

    int getTimeNeeded();

    int getTurnaroundTime();

    void doTask(int efficiency) throws TaskNotCompleteException;

}
