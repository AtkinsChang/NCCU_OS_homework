package edu.nccu.plsm.osproject.queue.message;

public class UpdateProducerResponse extends AbstractResponse {

    public UpdateProducerResponse(boolean success) {
        super(UPDATE_PRODUCER_RESPONSE, success);
    }

}
