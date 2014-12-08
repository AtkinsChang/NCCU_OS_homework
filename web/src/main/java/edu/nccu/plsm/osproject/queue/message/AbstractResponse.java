package edu.nccu.plsm.osproject.queue.message;

public abstract class AbstractResponse extends Message {

    private final boolean success;
    private String message = "";

    public AbstractResponse(String type, boolean success) {
        super(type);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
