package edu.nccu.plsm.osproject.queue.message;

public class UpdatePutLockResponse extends AbstractResponse {

    public UpdatePutLockResponse(boolean success) {
        super(UPDATE_PUT_LOCK_RESPONSE, success);
    }

}
