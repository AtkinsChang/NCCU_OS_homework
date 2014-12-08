package edu.nccu.plsm.osproject.queue.message;

public class UpdateConsumerResponse extends AbstractResponse {

    public UpdateConsumerResponse(boolean success) {
        super(UPDATE_CONSUMER_RESPONSE, success);
    }

}
