package edu.nccu.plsm.osproject.queue.message;

public class UpdateQueueRequest extends Message {

    private Integer capacity;
    private Integer maxPutTime;
    private Integer minPutTime;
    private Integer maxTakeTime;
    private Integer minTakeTime;

    public UpdateQueueRequest() {
        super(UPDATE_QUEUE_REQUEST);
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getMaxPutTime() {
        return maxPutTime;
    }

    public void setMaxPutTime(Integer maxPutTime) {
        this.maxPutTime = maxPutTime;
    }

    public Integer getMinPutTime() {
        return minPutTime;
    }

    public void setMinPutTime(Integer minPutTime) {
        this.minPutTime = minPutTime;
    }

    public Integer getMaxTakeTime() {
        return maxTakeTime;
    }

    public void setMaxTakeTime(Integer maxTakeTime) {
        this.maxTakeTime = maxTakeTime;
    }

    public Integer getMinTakeTime() {
        return minTakeTime;
    }

    public void setMinTakeTime(Integer minTakeTime) {
        this.minTakeTime = minTakeTime;
    }

}
