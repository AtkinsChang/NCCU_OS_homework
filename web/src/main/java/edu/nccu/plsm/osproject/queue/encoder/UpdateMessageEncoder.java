package edu.nccu.plsm.osproject.queue.encoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nccu.plsm.osproject.queue.message.UpdateStateResponse;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class UpdateMessageEncoder implements Encoder.Text<UpdateStateResponse> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(UpdateStateResponse updateStateResponse) throws EncodeException {

        try {
            return mapper.writeValueAsString(updateStateResponse);
        } catch (JsonProcessingException e) {
            throw new EncodeException(updateStateResponse, e.getMessage());
        }

        /*
        StringWriter stringWriter = new StringWriter();
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonArrayBuilder producerArrayBuilder = factory.createArrayBuilder();
        for (ProducerInfo producerInfo : updateMessage.getProducerInfos()) {
            JsonObject productionTime = factory.createObjectBuilder()
                    .add("max",producerInfo.getMaxProductionTime())
                    .add("min",producerInfo.getMinProductionTime())
                    .build();
            JsonObject taskExecutionTime = factory.createObjectBuilder()
                    .add("max",producerInfo.getMaxTaskExecutionTime())
                    .add("min",producerInfo.getMinTaskExecutionTime())
                    .build();
            JsonObject infoJson = factory.createObjectBuilder()
                    .add("name", producerInfo.getName())
                    .add("count", producerInfo.getTaskCreationCount())
                    .add("productionTime",productionTime)
                    .add("taskExecutionTime",taskExecutionTime)
                    .build();
            producerArrayBuilder.add(infoJson);
        }

        try (JsonGenerator jsonGenerator = Json.createGenerator(stringWriter)) {
            jsonGenerator.writeStartObject()
                    .write("type", updateMessage.getType())
                    .writeStartArray("")
                    .write("target", chatMessage.getTarget())
                    .write("message", chatMessage.getMessage())
                    .writeEnd();
        }*/
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
