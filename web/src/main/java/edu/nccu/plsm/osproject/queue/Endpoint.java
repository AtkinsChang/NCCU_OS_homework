package edu.nccu.plsm.osproject.queue;


import edu.nccu.plsm.osproject.queue.decoder.MessageDecoder;
import edu.nccu.plsm.osproject.queue.encoder.DefaultEncoder;
import edu.nccu.plsm.osproject.queue.message.AbstractResponse;
import edu.nccu.plsm.osproject.queue.message.UpdateConsumerRequest;
import edu.nccu.plsm.osproject.queue.message.UpdateConsumerResponse;
import edu.nccu.plsm.osproject.queue.message.UpdateProducerRequest;
import edu.nccu.plsm.osproject.queue.message.UpdateProducerResponse;
import edu.nccu.plsm.osproject.queue.message.Message;
import edu.nccu.plsm.osproject.queue.message.UpdatePutLockRequest;
import edu.nccu.plsm.osproject.queue.message.UpdatePutLockResponse;
import edu.nccu.plsm.osproject.queue.message.UpdateQueueRequest;
import edu.nccu.plsm.osproject.queue.message.UpdateQueueResponse;
import edu.nccu.plsm.osproject.queue.message.UpdateStateResponse;
import edu.nccu.plsm.osproject.queue.message.UpdateTakeLockRequest;
import edu.nccu.plsm.osproject.queue.message.UpdateTakeLockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(
        value = "/websocket",
        decoders = {MessageDecoder.class},
        encoders = {DefaultEncoder.class}
)
public class Endpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);

    @EJB
    private OSProjectBean osProjectBean;
    @Resource
    private ManagedScheduledExecutorService managedScheduledExecutorService;

    @OnOpen
    public void openConnection(Session session) throws IOException, EncodeException {
        LOGGER.info("Connection opened by {}.", session.getId());
        session.getBasicRemote().sendObject(createStateMessage());
        LOGGER.info("Sent: {}", osProjectBean.toString());
    }

    @OnMessage
    public void message(final Session session, Message msg) throws IOException, EncodeException {
        MDC.put("name", session.getId() + " - ");
        LOGGER.info("Received: {}", msg.getType());
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
            UpdateConsumerRequest req = (UpdateConsumerRequest)msg;
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
            response.setMessage(e.toString());
            return response;
        }
    }

    private UpdateProducerResponse updateProducer(Message msg) {
        try {
            UpdateProducerRequest req = (UpdateProducerRequest)msg;
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
            response.setMessage(e.toString());
            return response;
        }
    }

    private UpdateQueueResponse updateQueue(Message msg) {
        try {
            UpdateQueueRequest req = (UpdateQueueRequest)msg;
            osProjectBean.updateQueue(req.getCapacity(),
                    req.getMinPutTime(), req.getMaxPutTime(),
                    req.getMinTakeTime(), req.getMaxTakeTime());
            return new UpdateQueueResponse(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn("Failed to update queue", e);
            UpdateQueueResponse response = new UpdateQueueResponse(Boolean.FALSE);
            response.setMessage(e.toString());
            return response;
        }
    }

    private UpdatePutLockResponse updatePutLock(Message msg) {
        try {
            UpdatePutLockRequest req = (UpdatePutLockRequest)msg;
            if (req.getLock()) {
                osProjectBean.lockPut();
            } else {
                osProjectBean.unlockPut();
            }
            return new UpdatePutLockResponse(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn("Failed to update put lock", e);
            UpdatePutLockResponse response = new UpdatePutLockResponse(Boolean.FALSE);
            response.setMessage(e.toString());
            return response;
        }
    }

    private UpdateTakeLockResponse updateTakeLock(Message msg) {
        try {
            UpdateTakeLockRequest req = (UpdateTakeLockRequest)msg;
            if (req.getLock()) {
                osProjectBean.lockTake();
            } else {
                osProjectBean.unlockTake();
            }
            return new UpdateTakeLockResponse(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn("Failed to update take lock", e);
            UpdateTakeLockResponse response = new UpdateTakeLockResponse(Boolean.FALSE);
            response.setMessage(e.toString());
            return response;
        }
    }

}
