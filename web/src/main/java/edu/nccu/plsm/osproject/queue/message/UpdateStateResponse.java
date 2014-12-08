package edu.nccu.plsm.osproject.queue.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import edu.nccu.plsm.osproject.queue.message.entity.ConsumerJson;
import edu.nccu.plsm.osproject.queue.message.entity.ProducerJson;
import edu.nccu.plsm.osproject.queue.message.entity.QueueJson;
import edu.nccu.plsm.osproject.queue.message.entity.TaskJson;

import java.util.List;

public class UpdateStateResponse extends AbstractResponse {

    @JsonProperty("producers")
    final private List<ProducerJson> producerJsons;
    @JsonProperty("consumers")
    final private List<ConsumerJson> consumerJsons;
    @JsonProperty("tasks")
    final private List<TaskJson> taskJsons;
    @JsonProperty("queueInformation")
    final private QueueJson queueJson;

    public UpdateStateResponse(boolean success, List<ProducerJson> producerJsons, List<ConsumerJson> consumerJsons,
                               List<TaskJson> taskJsons, QueueJson queueJson) {
        super(UPDATE_STATE_RESPONSE, success);
        if(success) {
            this.producerJsons = producerJsons;
            this.consumerJsons = consumerJsons;
            int taskSize = taskJsons.size();
            if (taskSize > 10) {
                ImmutableList.Builder<TaskJson> builder = ImmutableList.builder();
                builder.addAll(taskJsons.subList(0, 4));
                builder.addAll(taskJsons.subList(taskSize - 6, taskSize - 1));
                this.taskJsons = builder.build();
            } else {
                this.taskJsons = taskJsons;
            }
            this.queueJson = queueJson;
        } else {
            this.producerJsons = null;
            this.consumerJsons = null;
            this.queueJson = null;
            this.taskJsons = null;
        }
    }

    public List<ProducerJson> getProducerJsons() {
        return producerJsons;
    }

    public List<ConsumerJson> getConsumerJsons() {
        return consumerJsons;
    }

    public List<TaskJson> getTaskJsons() {
        return this.taskJsons;
    }

    public QueueJson getQueueJson() {
        return this.queueJson;
    }
}
