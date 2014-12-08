package edu.nccu.plsm.osproject.queue.message;

public class UpdatePutLockRequest extends Message {

    private Boolean lock;

    public UpdatePutLockRequest() {
        super(UPDATE_PUT_LOCK_REQUEST);
    }

    public Boolean getLock() {
        return lock;
    }

    public void setLock(Boolean lock) {
        this.lock = lock;
    }

}
