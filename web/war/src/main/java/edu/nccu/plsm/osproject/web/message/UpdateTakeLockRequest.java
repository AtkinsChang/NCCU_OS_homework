package edu.nccu.plsm.osproject.web.message;

public class UpdateTakeLockRequest extends Message {

    private Boolean lock;

    public UpdateTakeLockRequest() {
        super(UPDATE_TAKE_LOCK_REQUEST);
    }

    public Boolean getLock() {
        return lock;
    }

    public void setLock(Boolean lock) {
        this.lock = lock;
    }

}
