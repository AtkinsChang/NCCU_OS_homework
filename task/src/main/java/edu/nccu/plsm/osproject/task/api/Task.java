package edu.nccu.plsm.osproject.task.api;

import edu.nccu.plsm.osproject.task.TaskNotCompleteException;

public interface Task {

    public String getCreatorName();

    void doTask(int efficiency) throws TaskNotCompleteException;

}
