package edu.nccu.plsm.osproject.task;

public class TaskNotCompleteException extends Exception {

    public TaskNotCompleteException(String message) {
        super(message);
    }

    public TaskNotCompleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
