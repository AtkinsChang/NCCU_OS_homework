package edu.nccu.plsm.osproject.queue.decoder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nccu.plsm.osproject.queue.message.UpdateConsumerRequest;
import edu.nccu.plsm.osproject.queue.message.UpdateProducerRequest;
import edu.nccu.plsm.osproject.queue.message.Message;
import edu.nccu.plsm.osproject.queue.message.UpdateStateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

public class MessageDecoder implements Decoder.Text<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);
    private static final ObjectMapper jsonParser;
    static {
        jsonParser = new ObjectMapper();
        jsonParser.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
    }
    private String type;
    private JsonNode parameters;

    @Override
    public Message decode(String s) throws DecodeException {
        LOGGER.info("Decoding {}", s);
        try {
            switch (type) {
                case Message.UPDATE_STATE_REQUEST:
                    return new UpdateStateRequest();
                case Message.UPDATE_PRODUCER_REQUEST:
                    return jsonParser.readValue(jsonParser.writeValueAsString(parameters), UpdateProducerRequest.class);
                case Message.UPDATE_CONSUMER_REQUEST:
                    return jsonParser.readValue(jsonParser.writeValueAsString(parameters), UpdateConsumerRequest.class);
                default:
                    //ignore
                    throw new DecodeException(s, "");
            }
        } catch (IOException e) {
            LOGGER.error("Object json mapping error", e);
            throw new DecodeException(s, "[Message] Can't decode.", e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        boolean decodes = false;
        try {
            JsonNode json = jsonParser.readTree(s);
            JsonNode type = json.get("type");
            parameters = json.get("parameters");
            if (type != null) {
                this.type = type.textValue();
                switch (this.type) {
                    case Message.UPDATE_STATE_REQUEST:
                        decodes = parameters == null;
                        break;
                    case Message.UPDATE_CONSUMER_REQUEST:
                    case Message.UPDATE_PRODUCER_REQUEST:
                    case Message.UPDATE_QUEUE_REQUEST:
                    case Message.UPDATE_PUT_LOCK_REQUEST:
                    case Message.UPDATE_TAKE_LOCK_REQUEST:
                        decodes = parameters != null;
                        break;
                    default:
                        LOGGER.warn("Wrong \"type\" field in json: {}", s);
                }
            } else {
                LOGGER.warn("Missing \"type\" field in json: {}", s);
            }
        } catch (IOException e) {
            LOGGER.warn("Error parsing json: parsing {}", s, e);
        }
        return decodes;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
