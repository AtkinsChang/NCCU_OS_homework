package edu.nccu.plsm.osproject.web.message;

public class Message {

    public static final String UPDATE_CONSUMER_REQUEST = "ucreq";
    public static final String UPDATE_PRODUCER_REQUEST = "upreq";
    public static final String UPDATE_QUEUE_REQUEST = "uqreq";
    public static final String UPDATE_PUT_LOCK_REQUEST = "plreq";
    public static final String UPDATE_TAKE_LOCK_REQUEST = "tlreq";
    public static final String UPDATE_STATE_REQUEST = "ureq";
    static final String UPDATE_CONSUMER_RESPONSE = "ucres";
    static final String UPDATE_PRODUCER_RESPONSE = "upres";
    static final String UPDATE_PUT_LOCK_RESPONSE = "uplres";
    static final String UPDATE_TAKE_LOCK_RESPONSE = "utlres";
    static final String UPDATE_QUEUE_RESPONSE = "uqres";
    static final String UPDATE_STATE_RESPONSE = "ures";


    private final String type;

    public Message(String type) {
        this.type = type;
    }


    public String getType() {
        return type;
    }
}
