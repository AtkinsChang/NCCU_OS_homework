package edu.nccu.plsm.osproject.web;


import edu.nccu.plsm.osproject.web.decoder.MessageDecoder;
import edu.nccu.plsm.osproject.web.encoder.DefaultEncoder;
import edu.nccu.plsm.osproject.web.message.AbstractResponse;
import edu.nccu.plsm.osproject.web.message.Message;
import edu.nccu.plsm.osproject.web.message.UpdateConsumerRequest;
import edu.nccu.plsm.osproject.web.message.UpdateConsumerResponse;
import edu.nccu.plsm.osproject.web.message.UpdateProducerRequest;
import edu.nccu.plsm.osproject.web.message.UpdateProducerResponse;
import edu.nccu.plsm.osproject.web.message.UpdatePutLockRequest;
import edu.nccu.plsm.osproject.web.message.UpdatePutLockResponse;
import edu.nccu.plsm.osproject.web.message.UpdateQueueRequest;
import edu.nccu.plsm.osproject.web.message.UpdateQueueResponse;
import edu.nccu.plsm.osproject.web.message.UpdateStateResponse;
import edu.nccu.plsm.osproject.web.message.UpdateTakeLockRequest;
import edu.nccu.plsm.osproject.web.message.UpdateTakeLockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StringWriter;

@ServerEndpoint(
        value = "/websocket",
        decoders = {MessageDecoder.class},
        encoders = {DefaultEncoder.class}
)
public class Endpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);

    @EJB
    private OSProjectBean osProjectBean;

    @OnOpen
    public void openConnection(Session session) throws IOException, EncodeException {
        if (osProjectBean == null) {
            LOGGER.error("fuck you CDI");
            try {
                Context ctx = new InitialContext();
                osProjectBean = (OSProjectBean) ctx.lookup("edu.nccu.plsm.osproject.web.OSProjectBean");
            } catch (NamingException e) {
                LOGGER.error("Context lookup failed", e);
            }
        }
        LOGGER.info("Connection opened by {}.", session.getId());
        session.getBasicRemote().sendObject(createStateMessage());
        LOGGER.info("Sent: {}", osProjectBean.toString());
    }

    @OnMessage
    public void message(final Session session, Message msg) throws IOException, EncodeException {
        MDC.put("name", session.getId() + " - ");
        LOGGER.trace("Received: {}", msg.getType());
        AbstractResponse response;
        switch (msg.getType()) {
            case Message.UPDATE_STATE_REQUEST:
                response = createStateMessage();
                break;
            case Message.UPDATE_CONSUMER_REQUEST:
                response = updateConsumer(msg);
                break;
            case Message.UPDATE_PRODUCER_REQUEST:
                response = updateProducer(msg);
                break;
            case Message.UPDATE_QUEUE_REQUEST:
                response = updateQueue(msg);
                break;
            case Message.UPDATE_TAKE_LOCK_REQUEST:
                response = updateTakeLock(msg);
                break;
            case Message.UPDATE_PUT_LOCK_REQUEST:
                response = updatePutLock(msg);
                break;
            default:
                throw new IllegalArgumentException(msg.getType());
        }
        session.getBasicRemote().sendObject(response);
    }

    private UpdateStateResponse createStateMessage() {
        try {
            return new UpdateStateResponse(true,
                    osProjectBean.getProducerJson(),
                    osProjectBean.getConsumerJson(),
                    osProjectBean.getTaskJson(),
                    osProjectBean.getQueueJson());
        } catch (Exception e) {
            LOGGER.warn("Error creating message", e);
            return new UpdateStateResponse(false, null, null, null, null);
        }
    }

    private UpdateConsumerResponse updateConsumer(Message msg) {
        try {
            UpdateConsumerRequest req = (UpdateConsumerRequest) msg;
            Boolean forceShutDown = req.getForceShutdown();
            if (forceShutDown != null) {
                osProjectBean.shutdownConsumer(req.getName(), forceShutDown);
            } else {
                osProjectBean.updateConsumer(req.getName(), req.getEfficiency());
            }
            return new UpdateConsumerResponse(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn("Failed to update consumer", e);
            UpdateConsumerResponse response = new UpdateConsumerResponse(Boolean.FALSE);
            response.setMessage(throwableToString(e));
            return response;
        }
    }

    private UpdateProducerResponse updateProducer(Message msg) {
        try {
            UpdateProducerRequest req = (UpdateProducerRequest) msg;
            Boolean forceShutDown = req.getForceShutdown();
            if (forceShutDown != null) {
                osProjectBean.shutdownProducer(req.getName(), forceShutDown);
            } else {
                osProjectBean.updateProducer(req.getName(),
                        req.getMinProductionTime(), req.getMaxProductionTime(),
                        req.getMinTaskExecutionTime(), req.getMaxTaskExecutionTime());
            }
            return new UpdateProducerResponse(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn("Failed to update consumer", e);
            UpdateProducerResponse response = new UpdateProducerResponse(Boolean.FALSE);
            response.setMessage(throwableToString(e));
            return response;
        }
    }

    private UpdateQueueResponse updateQueue(Message msg) {
        try {
            UpdateQueueRequest req = (UpdateQueueRequest) msg;
            osProjectBean.updateQueue(req.getCapacity(),
                    req.getMinPutTime(), req.getMaxPutTime(),
                    req.getMinTakeTime(), req.getMaxTakeTime());
            return new UpdateQueueResponse(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn("Failed to update queue", e);
            UpdateQueueResponse response = new UpdateQueueResponse(Boolean.FALSE);
            response.setMessage(throwableToString(e));
            return response;
        }
    }

    private UpdatePutLockResponse updatePutLock(Message msg) {
        try {
            UpdatePutLockRequest req = (UpdatePutLockRequest) msg;
            if (req.getLock()) {
                osProjectBean.lockPut();
            } else {
                osProjectBean.unlockPut();
            }
            return new UpdatePutLockResponse(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn("Failed to update put lock", e);
            UpdatePutLockResponse response = new UpdatePutLockResponse(Boolean.FALSE);

            response.setMessage(throwableToString(e));
            return response;
        }
    }

    private UpdateTakeLockResponse updateTakeLock(Message msg) {
        try {
            UpdateTakeLockRequest req = (UpdateTakeLockRequest) msg;
            if (req.getLock()) {
                osProjectBean.lockTake();
            } else {
                osProjectBean.unlockTake();
            }
            return new UpdateTakeLockResponse(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn("Failed to update take lock", e);
            UpdateTakeLockResponse response = new UpdateTakeLockResponse(Boolean.FALSE);
            response.setMessage(throwableToString(e));
            return response;
        }
    }

    private String throwableToString(Throwable t) {
        StringWriter sw = new StringWriter();
        sw.append(t.toString());
        Throwable nt = t.getCause();
        while (nt != null) {
            sw.append("\n");
            sw.append(nt.toString());
            nt = nt.getCause();
        }
        return sw.toString();
    }

}
