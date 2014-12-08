package edu.nccu.plsm.osproject.queue.message;


public class UpdateConsumerRequest extends Message {

    private String name;
    private Integer efficiency;
    private Boolean forceShutdown;

    public UpdateConsumerRequest() {
        super(UPDATE_CONSUMER_REQUEST);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Integer efficiency) {
        this.efficiency = efficiency;
    }

    public Boolean getForceShutdown() {
        return forceShutdown;
    }

    public void setForceShutdown(Boolean forceShutdown) {
        this.forceShutdown = forceShutdown;
    }

}
