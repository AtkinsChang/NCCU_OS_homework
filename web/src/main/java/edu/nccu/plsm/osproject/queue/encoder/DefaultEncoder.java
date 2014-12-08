package edu.nccu.plsm.osproject.queue.encoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nccu.plsm.osproject.queue.message.AbstractResponse;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class DefaultEncoder implements Encoder.Text<AbstractResponse> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(AbstractResponse abstractResponse) throws EncodeException {
        try {
            return mapper.writeValueAsString(abstractResponse);
        } catch (JsonProcessingException e) {
            throw new EncodeException(abstractResponse, e.getMessage());
        }
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
