package edu.nccu.plsm.osproject.web.message;

public class UpdateTakeLockResponse extends AbstractResponse {

    public UpdateTakeLockResponse(boolean success) {
        super(UPDATE_TAKE_LOCK_RESPONSE, success);
    }

}