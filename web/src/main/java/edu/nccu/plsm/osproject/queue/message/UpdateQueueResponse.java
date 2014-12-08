package edu.nccu.plsm.osproject.queue.message;

public class UpdateQueueResponse extends AbstractResponse {

    public UpdateQueueResponse(boolean success) {
        super(UPDATE_QUEUE_RESPONSE, success);
    }

}
