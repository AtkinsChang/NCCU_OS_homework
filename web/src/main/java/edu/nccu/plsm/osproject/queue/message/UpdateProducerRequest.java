package edu.nccu.plsm.osproject.queue.message;


public class UpdateProducerRequest extends Message {

    private String name;
    private Integer maxProductionTime;
    private Integer minProductionTime;
    private Integer maxTaskExecutionTime;
    private Integer minTaskExecutionTime;
    private Boolean forceShutdown;

    public Boolean getForceShutdown() {
        return forceShutdown;
    }

    public void setForceShutdown(Boolean forceShutdown) {
        this.forceShutdown = forceShutdown;
    }

    public UpdateProducerRequest() {
        super(UPDATE_PRODUCER_REQUEST);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxProductionTime() {
        return maxProductionTime;
    }

    public void setMaxProductionTime(Integer maxProductionTime) {
        this.maxProductionTime = maxProductionTime;
    }

    public Integer getMinProductionTime() {
        return minProductionTime;
    }

    public void setMinProductionTime(Integer minProductionTime) {
        this.minProductionTime = minProductionTime;
    }

    public Integer getMaxTaskExecutionTime() {
        return maxTaskExecutionTime;
    }

    public void setMaxTaskExecutionTime(Integer maxTaskExecutionTime) {
        this.maxTaskExecutionTime = maxTaskExecutionTime;
    }

    public Integer getMinTaskExecutionTime() {
        return minTaskExecutionTime;
    }

    public void setMinTaskExecutionTime(Integer minTaskExecutionTime) {
        this.minTaskExecutionTime = minTaskExecutionTime;
    }

}
